/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.fountain

import org.kotlinbitcointools.ur.utilities.RandomSampler
import org.kotlinbitcointools.ur.utilities.RandomXoshiro256StarStar
import java.nio.ByteBuffer

public fun chooseDegree(sequenceLength: Int, rng: RandomXoshiro256StarStar): Int {
    val degreeProbabilities: List<Double> = (1..sequenceLength).map { 1 / it.toDouble() }.toList()
    val randomSampler = RandomSampler(degreeProbabilities)
    return randomSampler.next(rng) + 1
}

public fun shuffle(items: List<Int>, rng: RandomXoshiro256StarStar, count: Int): List<Int> {
    val remaining = items.toMutableList()
    val result = mutableListOf<Int>()

    while (result.size < count) {
        val index = rng.nextInt(0, remaining.size)
        val item = remaining.removeAt(index)
        result.add(item)
    }

    return result
}

// TODO: Look into better understanding this function
public fun chooseFragments(sequenceNumber: Long, sequenceLength: Int, checksum: Int): Set<Int> {
    if (sequenceNumber <= sequenceLength) {
        return setOf((sequenceNumber - 1).toInt())
    } else {
        val buffer = ByteBuffer.allocate(Integer.BYTES * 2)
        buffer.putInt(sequenceNumber.toInt())
        buffer.putInt(checksum)

        val rng = RandomXoshiro256StarStar(buffer.array())
        val degree = chooseDegree(sequenceLength, rng)
        val indexes = (0 until sequenceLength).toList()
        val shuffledIndexes = shuffle(indexes, rng, degree)

        return shuffledIndexes.toSet()
    }
}
