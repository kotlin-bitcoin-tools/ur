/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcointools.ur

public class UREncoder {
    public companion object {
        public fun encode(ur: UR): String {
            val encoded: String = Bytewords.encodeMinimal(ur.cbor)
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
