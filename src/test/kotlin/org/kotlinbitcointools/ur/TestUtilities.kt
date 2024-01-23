package org.kotlinbitcointools.ur

import org.kotlinbitcointools.ur.utilities.RandomXoshiro256StarStar

internal fun makeMessage(length: Int, seed: String): ByteArray {
    val rng = RandomXoshiro256StarStar(seed)
    val message = ByteArray(length)
    rng.nextData(message)
    return message
}
