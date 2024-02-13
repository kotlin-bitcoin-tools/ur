/*
 * Copyright 2023-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.registry

import org.kotlinbitcointools.ur.UnsupportedRegistryTypeException

public enum class RegistryType(public val type: String) {
    BYTES("bytes");

    public companion object {
        public fun fromString(type: String): RegistryType {
            return when (type) {
                "bytes" -> BYTES
                else -> throw UnsupportedRegistryTypeException("Unsupported registry type: $type")
            }
        }
    }
}
