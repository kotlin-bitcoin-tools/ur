/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.fountain

import org.kotlinbitcointools.ur.utilities.RandomSampler
import org.kotlinbitcointools.ur.utilities.RandomXoshiro256StarStar
import org.kotlinbitcointools.ur.utilities.crc32Checksum
import java.nio.ByteBuffer
import java.util.Arrays
import kotlin.math.ceil

// Implements Luby transform code rateless coding
// https://en.wikipedia.org/wiki/Luby_transform_code

public class FountainEncoder(
    private val message: ByteArray,
    private val maximumFragmentLength: Int,
    private val minimumFragmentLength: Int = 10,
    private val firstSequenceNumber: Long = 0
) {
    private val messageLength: Int = message.size
    public val checksum: Int = crc32Checksum(message)
    private val fragmentLength: Int = findNominalFragmentLength(messageLength, minimumFragmentLength, maximumFragmentLength)
    private val segments: List<ByteArray> = partitionMessage(message, fragmentLength)
    private val sequenceLength: Int = segments.size
    private var sequenceNumber: Long = firstSequenceNumber
    public var isSinglePart: Boolean = sequenceLength == 1

    init {
        // TODO: this check is actually against an unsigned int in the Swift implementation
        require(messageLength <= Int.MAX_VALUE) { "Message is too long" }
    }

    private fun chooseFragments(sequenceNumber: Long): List<Int> {
        return if (sequenceNumber <= sequenceLength) {
            // TODO: Why is this Long cast back into an Int? If the sequenceNumber can be as high as an Int,
            //       then why not have a List<Long> for the partIndexes variable?
            listOf<Int>(sequenceNumber.toInt() - 1)
        } else {
            val buffer = ByteBuffer.allocate(Integer.BYTES * 2)
            buffer.putInt(sequenceNumber.toInt())
            buffer.putInt(checksum)
            val seed = buffer.array()

            val rng = RandomXoshiro256StarStar(seed)
            val degree: Int = chooseDegree(sequenceLength, rng)
            val indexes: List<Int> = List(sequenceLength) { it }
            val shuffledIndexes: List<Int> = shuffle(indexes, rng)
            shuffledIndexes.subList(0, degree)
        }
    }

    private fun chooseDegree(sequenceLength: Int, rng: RandomXoshiro256StarStar): Int {
        val degreeProbabilities: List<Double> = (1..sequenceLength).map { 1 / it.toDouble() }
        val sampler = RandomSampler(degreeProbabilities)
        return sampler.next(rng) + 1
    }

    private fun shuffle(indexes: List<Int>, rng: RandomXoshiro256StarStar): List<Int> {
        val remaining: MutableList<Int> = indexes.toMutableList()
        val shuffled: MutableList<Int> = mutableListOf()

        while (remaining.isNotEmpty()) {
            val index = rng.nextInt(0, remaining.size)
            val item = remaining.removeAt(index)
            shuffled.add(item)
        }

        return shuffled
    }

    public companion object {
        internal fun findNominalFragmentLength(
            messageLength: Int,
            minimumFragmentLength: Int,
            maximumFragmentLength: Int
        ): Int {
            require(messageLength > 0) { "Message length must be greater than 0" }
            require(minimumFragmentLength > 0) { "Minimum fragment length must be greater than 0" }
            require(maximumFragmentLength >= minimumFragmentLength) { "Maximum fragment length must be greater than or equal to minimum fragment length" }

            // TODO: this feels odd... because the remainder is dropped, we might get a maximum fragment count that is too low
            //       to contain the whole message no? Say messageLength = 14, minimumFragmentLength = 4, we would get a
            //       maximumFragmentCount of 3, but that's not enough to contain the whole message (4 * 3 = 12).
            val maximumFragmentCount = (messageLength / minimumFragmentLength).coerceAtLeast(1)
            var fragmentLength = 0
            (1..maximumFragmentCount).forEach { fragmentCount ->
                fragmentLength = ceil(messageLength.toDouble() / fragmentCount).toInt()
                if (fragmentLength <= maximumFragmentLength) {
                    return fragmentLength
                }
            }
            return fragmentLength
        }

        internal fun partitionMessage(message: ByteArray, fragmentLength: Int): List<ByteArray> {
            val fragmentCount = ceil(message.size / fragmentLength.toDouble()).toInt()

            return (0 until fragmentCount).map { fragmentIndex ->
                val start = fragmentIndex * fragmentLength
                val end = start + fragmentLength
                Arrays.copyOfRange(message, start, end)
            }
        }
    }
}
