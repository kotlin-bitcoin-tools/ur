/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcointools.ur

public class UR(public val registryType: RegistryType, public val cborBytes: ByteArray) {
    /**
     * Returns the UR as a string, always a single-part UR.
     */
    public override fun toString(): String {
        return UREncoder.encodeSinglePartUR(this)
    }

    public companion object {
        public const val PREFIX: String = "ur"
    }
}

public fun Char.isValidURCharacter(): Boolean {
    return this in 'A'..'Z' || this in 'a'..'z' || this in '0'..'9' || this == '-'
}

public fun String.isURType(): Boolean {
    return this.all { it.isValidURCharacter() }
}
