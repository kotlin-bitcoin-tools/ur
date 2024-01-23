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
