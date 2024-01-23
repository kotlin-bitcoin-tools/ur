/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.utilities

import java.security.MessageDigest
import java.util.zip.CRC32

public fun crc32Checksum(input: ByteArray): Int {
    val crc32 = CRC32()
    crc32.update(input)
    return crc32.value.toInt()
}

public fun sha256(input: ByteArray): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(input)
}
