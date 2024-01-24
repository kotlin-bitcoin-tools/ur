/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.fountain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import org.kotlinbitcointools.ur.utilities.crc32Checksum
import java.util.Arrays
import kotlin.math.ceil

// Implements Luby transform code rateless coding
// https://en.wikipedia.org/wiki/Luby_transform_code

public class FountainEncoder(
    private val message: ByteArray,
    private val maximumFragmentLength: Int,
    private val minimumFragmentLength: Int,
    private val firstSequenceNumber: Long
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

    public class Part(
        public val sequenceNumber: Long,
        public val sequenceLength: Int,
        public val messageLength: Int,
        public val checksum: Int,
        public val data: ByteArray
    ) {
        public fun toCborBytes(): ByteArray {
            val cborMapper = ObjectMapper(CBORFactory())
            val array = arrayOf(sequenceNumber, sequenceLength, messageLength, checksum, data)
            return cborMapper.writeValueAsBytes(array)
        }

        public companion object {
            public fun fromCborBytes(cborBytes: ByteArray): Part {
                val cborMapper = ObjectMapper(CBORFactory())
                val array: Array<Any> = cborMapper.readValue(cborBytes, Array<Any>::class.java)

                return Part(
                    sequenceNumber = (array[0] as Number).toLong(),
                    sequenceLength = (array[1] as Number).toInt(),
                    messageLength = (array[2] as Number).toInt(),
                    checksum = (array[3] as Number).toInt(),
                    data = array[4] as ByteArray
                )
            }
        }
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

        // TODO: Throw if the arrays passed are not of the same size
        public fun xor(a: ByteArray, b: ByteArray): ByteArray {
            val result = ByteArray(a.size)
            for (i in result.indices) {
                result[i] = ((a[i].toInt()) xor (b[i].toInt())).toByte()
            }

            return result
        }
    }
}
