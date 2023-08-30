/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcointools.ur

import org.bitcointools.ur.fountain.FountainEncoder

public class UREncoder(
    private val ur: UR,
    maximumFragmentLength: Int,
    minimumFragmentLength: Int = 10,
    firstSequenceNumber: Long = 0,
) {
    private val fountainEncoder: FountainEncoder = FountainEncoder(
        ur.cborBytes,
        maximumFragmentLength,
        minimumFragmentLength,
        firstSequenceNumber
    )


    public companion object {
        public fun encodeSinglePartUR(ur: UR): String {
            val encoded: String = Bytewords.encodeMinimal(ur.cborBytes)
            val urBody = URBody.Single(encoded)
            return encodeURI(ur.registryType, urBody)
        }

        private fun encodeURI(type: RegistryType, body: URBody): String {
            return when (body) {
                is URBody.Single -> "${UR.PREFIX}:${type.type}/${body.message}"
                is URBody.FountainCodePart -> TODO()
            }
        }
    }
}

public sealed class URBody {
    public data class Single(public val message: String): URBody()
    public data class FountainCodePart(public val sequence: ULong?, public val message: String): URBody()
}
