/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur

import org.kotlinbitcointools.ur.fountain.FountainEncoder
import org.kotlinbitcointools.ur.registry.RegistryType

public class UREncoder(
    private val ur: UR,
    maximumFragmentLength: Int,
    minimumFragmentLength: Int,
    firstSequenceNumber: Int = 0,
) {
    private val fountainEncoder: FountainEncoder = FountainEncoder(
        ur.cborBytes,
        maximumFragmentLength,
        minimumFragmentLength,
        firstSequenceNumber
    )

    public fun nextPart(): String {
        if (fountainEncoder.isSinglePart) {
            return encode(ur)
        } else {
            val part: FountainEncoder.Part = fountainEncoder.nextPart()
            return encodePart(ur.registryType, part)
        }

    }

    public companion object {
        /**
         * Encode a single-part UR.
         */
        public fun encode(ur: UR): String {
            val body: String = Bytewords.encode(ur.cborBytes, Bytewords.Style.MINIMAL)
            return encodeURI(ur.registryType, URBody.Single(body))
        }

        private fun encodePart(type: RegistryType, part: FountainEncoder.Part): String {
            val message: String = Bytewords.encode(part.toCborBytes(), Bytewords.Style.MINIMAL)
            val sequence: String = "${part.sequenceNumber}-${part.sequenceLength}"
            return encodeURI(type, URBody.FountainCodePart(sequence, message))
        }

        private fun encodeURI(type: RegistryType, body: URBody): String {
            return when (body) {
                is URBody.Single           -> "${UR.PREFIX}:${type.type}/${body.message}"
                is URBody.FountainCodePart -> "${UR.PREFIX}:${type.type}/${body.sequence}/${body.message}"
            }
        }
    }
}

public sealed class URBody {
    public data class Single(public val message: String): URBody()
    public data class FountainCodePart(public val sequence: String, public val message: String): URBody()
}
