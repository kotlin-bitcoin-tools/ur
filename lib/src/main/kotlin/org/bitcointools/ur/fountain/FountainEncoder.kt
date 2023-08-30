/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcointools.ur.fountain

import java.util.Arrays
import java.util.zip.CRC32
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
    public val checksum: Long = getCRC32Checksum(message)
    private val fragmentLength: Int = findNominalFragmentLength(messageLength, minimumFragmentLength, maximumFragmentLength)
    private val segments: List<ByteArray> = partitionMessage(message, fragmentLength)
    private val sequenceLength: Int = segments.size

    init {
        // TODO: this check is actually against an unsigned int in the Swift implementation
        require(messageLength <= Int.MAX_VALUE) { "Message is too long" }
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

public fun getCRC32Checksum(data: ByteArray): Long {
    val crc32 = CRC32()
    crc32.update(data)
    return crc32.value
}
