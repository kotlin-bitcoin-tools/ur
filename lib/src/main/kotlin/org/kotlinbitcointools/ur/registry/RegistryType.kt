/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.registry

public enum class RegistryType(public val type: String) {
    BYTES("bytes"),
    CRYPTO_PSBT("crypto-psbt"),
}
