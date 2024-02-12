package org.kotlinbitcointools.ur

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import org.kotlinbitcointools.ur.registry.RegistryType
import org.kotlinbitcointools.ur.utilities.RandomXoshiro256StarStar

internal fun makeMessage(length: Int, seed: String): ByteArray {
    val rng = RandomXoshiro256StarStar(seed)
    val message = ByteArray(length)
    rng.nextData(message)
    return message
}

internal fun makeMessageUR(length: Int, seed: String): UR {
    val message = makeMessage(length, seed)
    val cborMapper = ObjectMapper(CBORFactory())
    val cborBytes: ByteArray = cborMapper.writeValueAsBytes(message)
    return UR(RegistryType.BYTES, cborBytes)
}
