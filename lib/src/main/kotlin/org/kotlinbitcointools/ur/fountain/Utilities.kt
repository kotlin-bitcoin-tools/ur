/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.fountain

import org.kotlinbitcointools.ur.utilities.RandomSampler
import org.kotlinbitcointools.ur.utilities.RandomXoshiro256StarStar

public fun chooseDegree(sequenceLength: Int, rng: RandomXoshiro256StarStar): Int {
    val degreeProbabilities: List<Double> = (1..sequenceLength).map { 1 / it.toDouble() }.toList()
    val randomSampler = RandomSampler(degreeProbabilities)
    return randomSampler.next(rng) + 1
}

// This is the Hummingbird implementation.
public fun hummingBirdShuffle(items: List<Int>, rng: RandomXoshiro256StarStar): List<Int> {
    val remaining: MutableList<Int> = items.toMutableList()
    val shuffled: MutableList<Int> = mutableListOf()

    while (remaining.isNotEmpty()) {
        val index = rng.nextInt(0, remaining.size)
        val item: Int = remaining.removeAt(index)
        shuffled.add(item)
    }

    return shuffled
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
