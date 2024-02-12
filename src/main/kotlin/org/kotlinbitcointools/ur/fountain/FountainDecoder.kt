/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.fountain

import org.kotlinbitcointools.ur.fountain.FountainEncoder.Companion.xor
import org.kotlinbitcointools.ur.utilities.crc32Checksum
import java.util.TreeMap

// Refer to Decoder section of this document for in-depth explanation of the workflow:
// https://github.com/BlockchainCommons/Research/blob/master/papers/bcr-2024-001-multipart-ur.md#6-the-decoder
public class FountainDecoder {
    // NOTE: The Swift and Java implementations use a Map<Set<Int>, Part> to store simple parts but this feels like a
    //       small mistake: simple parts can only have one fragment index, so a Map<Int, Part> feels more appropriate.
    //       This is also how the Rust ur-rs implementation does it.
    private val simpleParts: MutableMap<Int, Part> = TreeMap()
    private var mixedParts: MutableMap<Set<Int>, Part> = mutableMapOf()
    private val queuedParts: MutableList<Part> = mutableListOf()

    private var expectedFragmentIndexes: Set<Int>? = null
    private val receivedFragmentIndexes: MutableSet<Int> = mutableSetOf()
    private var expectedFragmentLen = 0
    private var expectedMessageLen = 0
    private var expectedChecksum: Long = 0

    // TODO: Write up what is this variable really used for
    private var lastFragmentIndexes: Set<Int>? = null

    public var result: Result<ByteArray>? = null

    // TODO: The Rust ur-rs implementation returns a Result instead, which I think is more elegant here
    //       because the boolean returned by this function is true if the part was processed and false
    //       if it was not _or_ if it was invalid _or_ if the decoder is already done. This is a bit
    //       confusing.
    public fun receivePart(encoderPart: FountainEncoder.Part): Boolean {
        // Don't process the part if we are already done
        if (this.result != null) return false

        // Don't continue if this part is not valid
        if (!isPartValid(encoderPart)) return false

        // Add this part to the queue
        val fragmentIndexes: Set<Int> = chooseFragments(encoderPart.sequenceNumber, encoderPart.sequenceLength, encoderPart.checksum)
        val part = Part(encoderPart, fragmentIndexes)
        lastFragmentIndexes = fragmentIndexes
        enqueue(part)

        // Process the queue until the message is reconstructed in full or the queue is empty
        processQueue()

        // Keep track of how many parts we've processed
        // TODO: Look into where this is used and why we should expose it in the API
        // processedPartsCount += 1
        return true
    }

    private fun enqueue(part: Part): Unit {
        queuedParts.add(part)
    }

    private fun processQueue(): Unit {
        // Process the queue until the message is reconstructed in full or the queue is empty
        while (queuedParts.isNotEmpty() && result == null) {
            val part: Part = queuedParts.removeAt(0)
            if (part.isSimple) {
                processSimplePart(part)
            } else {
                processMixedPart(part)
            }
        }
    }

    private fun processSimplePart(part: Part): Unit {
        require(part.isSimple) { "Part must be simple" }

        // Don't process duplicate parts
        val fragmentIndex: Int = part.fragmentIndexes.first()
        if (fragmentIndex in receivedFragmentIndexes) return

        // Record this part
        simpleParts.put(fragmentIndex, part)
        receivedFragmentIndexes.add(fragmentIndex)

        // If we've received all the parts, return a Result
        if (receivedFragmentIndexes == expectedFragmentIndexes) {
            // Reassemble the message from its fragments. Note that the simpleParts map is already sorted in ascending
            // order by fragment index because it's a TreeMap
            val fragments: List<ByteArray> = simpleParts.values.map { it.data }
            val message: ByteArray = joinFragments(fragments, expectedMessageLen)

            // If the checksum of the reassembled message matches the expected checksum, set result to success
            result = if (crc32Checksum(message) == expectedChecksum) {
                Result.success(message)
            } else {
                Result.failure(Exception("Checksum mismatch"))
            }
        } else {
            // If we haven't received all the parts, attempt to reduce all mixed parts by this part
            reduceMixed(part)
        }
    }

    private fun processMixedPart(part: Part): Unit {
        // Don't process duplicate parts
        if (part.fragmentIndexes in mixedParts.keys) return

        // Reduce this part by all the others
        val allParts: List<Part> = simpleParts.values + mixedParts.values
        val reducedPart: Part = allParts.fold(part) { acc, reduceBy -> reducePart(acc, reduceBy) }

        // If the reduced part is now simple, enqueue it
        if (reducedPart.isSimple) {
            enqueue(reducedPart)
        } else {
            // If the reduced part is still mixed, reduce all the mixed parts by it
            reduceMixed(reducedPart)
            // Record this new mixed part
            mixedParts.put(reducedPart.fragmentIndexes, reducedPart)
        }
    }

    // Join fragments into a single message, discarding any padding (by taking the first messageLength bytes)
    public fun joinFragments(fragments: List<ByteArray>, messageLength: Int): ByteArray {
        val combinedFragments: List<Byte> = fragments.flatMap { it.toList() }.take(messageLength)
        return combinedFragments.toByteArray()
    }

    private fun reduceMixed(by: Part): Unit {
        // Reduce all mixed parts by the given part
        val reducedParts: List<Part> = mixedParts.map { (_, part) ->
            reducePart(part, by)
        }

        val newMixed: MutableMap<Set<Int>, Part> = mutableMapOf()
        reducedParts.forEach { reducedPart ->
            if (reducedPart.isSimple) {
                enqueue(reducedPart)
            } else {
                newMixed.put(reducedPart.fragmentIndexes, reducedPart)
            }
        }
        this.mixedParts = newMixed
    }

    // Reduce part a by part b. For example, if part a is [1, 2, 3] and part b is [2, 3], the result is [1]
    private fun reducePart(partA: Part, partB: Part): Part {
        // If the fragments mixed into b are a strict subset of those in a, reduce a by b
        if (partA.fragmentIndexes.containsAll(partB.fragmentIndexes)) {
            // The new fragments in the revised part are Set(a) - Set(b)
            val newIndexes: Set<Int> = partA.fragmentIndexes - partB.fragmentIndexes
            // The new data in the revised part is a XOR b
            val reducedData = xor(partA.data, partB.data)

            return Part(reducedData, newIndexes)
        } else {
            // If a is not reducible by b, simply return a
            return partA
        }
    }

    private fun isPartValid(part: FountainEncoder.Part): Boolean {
        // If this is the first part we see, set the expected values
        if (expectedFragmentIndexes == null) {
            expectedFragmentIndexes = (0..<part.sequenceLength).toSet()
            expectedFragmentLen = part.data.size
            expectedMessageLen = part.messageLength
            expectedChecksum = part.checksum
        } else {
            // If this part doesn't match the expected values, do not process it (return false)
            if (part.sequenceLength != expectedFragmentIndexes!!.size) return false
            if (part.data.size != expectedFragmentLen) return false
            if (part.messageLength != expectedMessageLen) return false
            if (part.checksum != expectedChecksum) return false
        }
        // This part should be processed
        return true
    }

    public class Part(public val data: ByteArray, public val fragmentIndexes: Set<Int>) {

        public constructor(
            part: FountainEncoder.Part, fragmentIndexes: Set<Int>
        ) : this(part.data, fragmentIndexes)

        public val isSimple: Boolean
            get() = fragmentIndexes.size == 1
    }
}
