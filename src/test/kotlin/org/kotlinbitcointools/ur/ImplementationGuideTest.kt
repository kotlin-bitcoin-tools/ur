package org.kotlinbitcointools.ur

import com.google.common.primitives.Ints
import org.kotlinbitcointools.ur.fountain.FountainDecoder
import org.kotlinbitcointools.ur.fountain.FountainEncoder
import org.kotlinbitcointools.ur.fountain.chooseDegree
import org.kotlinbitcointools.ur.fountain.chooseFragments
import org.kotlinbitcointools.ur.fountain.shuffle
import org.kotlinbitcointools.ur.utilities.RandomSampler
import org.kotlinbitcointools.ur.utilities.RandomXoshiro256StarStar
import org.kotlinbitcointools.ur.utilities.crc32Checksum
import org.kotlinbitcointools.ur.utilities.sha256
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// https://github.com/BlockchainCommons/Research/blob/master/papers/bcr-2024-001-multipart-ur.md

@OptIn(ExperimentalStdlibApi::class)
class ImplementationGuideTest {
    @Test
    fun `CRC-32 checksum`() {
        val string: String = "Wolf"
        val checksum = crc32Checksum(string.encodeToByteArray())
        println(checksum)
        println(checksum.toString(16))
        println(checksum.toHexString())
    }

    @Test
    fun `CRC-32 checksum 2`() {
        val string =
            "916ec65cf77cadf55cd7f9cda1a1030026ddd42e905b77adc36e4f2d3ccba44f7f04f2de44f42d84c374a0e149136f25b01852545961d55f7f7a8cde6d0e2ec43f3b2dcb644a2209e8c9e34af5c4747984a5e873c9cf5f965e25ee29039fdf8ca74f1c769fc07eb7ebaec46e0695aea6cbd60b3ec4bbff1b9ffe8a9e7240129377b9d3711ed38d412fbb4442256f1e6f595e0fc57fed451fb0a0101fb76b1fb1e1b88cfdfdaa946294a47de8fff173f021c0e6f65b05c0a494e50791270a0050a73ae69b6725505a2ec8a5791457c9876dd34aadd192a53aa0dc66b556c0c215c7ceb8248b717c22951e65305b56a3706e3e86eb01c803bbf915d80edcd64d4d41977fa6f78dc07eecd072aae5bc8a852397e06034dba6a0b570797c3a89b16673c94838d884923b8186ee2db5c98407cab15e13678d072b43e406ad49477c2e45e85e52ca82a94f6df7bbbe7afbed3a3a830029f29090f25217e48d1f42993a640a67916aa7480177354cc7440215ae41e4d02eae9a191233a6d4922a792c1b7244aa879fefdb4628dc8b0923568869a983b8c661ffab9b2ed2c149e38d41fba090b94155adbed32f8b18142ff0d7de4eeef2b04adf26f2456b46775c6c20b37602df7da179e2332feba8329bbb8d727a138b4ba7a503215eda2ef1e953d89383a382c11d3f2cad37a4ee59a91236a3e56dcf89f6ac81dd4159989c317bd649d9cbc617f73fe10033bd288c60977481a09b343d3f676070e67da757b86de27bfca74392bac2996f7822a7d8f71a489ec6180390089ea80a8fcd6526413ec6c9a339115f111d78ef21d456660aa85f790910ffa2dc58d6a5b93705caef1091474938bd312427021ad1eeafbd19e0d916ddb111fabd8dcab5ad6a6ec3a9c6973809580cb2c164e26686b5b98cfb017a337968c7daaa14ae5152a067277b1b3902677d979f8e39cc2aafb3bc06fcf69160a853e6869dcc09a11b5009f91e6b89e5b927ab1527a735660faa6012b420dd926d940d742be6a64fb01cdc0cff9faa323f02ba41436871a0eab851e7f5782d10fbefde2a7e9ae9dc1e5c2c48f74f6c824ce9ef3c89f68800d44587bedc4ab417cfb3e7447d90e1e417e6e05d30e87239d3a5d1d45993d4461e60a0192831640aa32dedde185a371ded2ae15f8a93dba8809482ce49225daadfbb0fec629e23880789bdf9ed73be57fa84d555134630e8d0f7df48349f29869a477c13ccca9cd555ac42ad7f568416c3d61959d0ed568b2b81c7771e9088ad7fd55fd4386bafbf5a528c30f107139249357368ffa980de2c76ddd9ce4191376be0e6b5170010067e2e75ebe2d2904aeb1f89d5dc98cd4a6f2faaa8be6d03354c990fd895a97feb54668473e9d942bb99e196d897e8f1b01625cf48a7b78d249bb4985c065aa8cd1402ed2ba1b6f908f63dcd84b66425df".hexToByteArray(
                HexFormat.Default
            )
        val checksum = crc32Checksum(string)
        println(checksum)
        println(checksum.toString(16))
        println(checksum.toHexString())
    }

    @Test
    fun `SHA-256`() {
        val input = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".encodeToByteArray()
        val sha256 = sha256(input)
        println(sha256.toHexString())
    }

    @Test
    fun `Random Number Generator 1`() {
        val rng = RandomXoshiro256StarStar("Wolf")
        val numbers = List(100) { rng.nextLong().toULong() % 100u }
        val expectedNumbers = listOf(42, 81, 85, 8, 82, 84, 76, 73, 70, 88, 2, 74, 40, 48, 77, 54, 88, 7, 5, 88, 37, 25, 82, 13, 69, 59, 30, 39, 11, 82, 19, 99, 45, 87, 30, 15, 32, 22, 89, 44, 92, 77, 29, 78, 4, 92, 44, 68, 92, 69, 1, 42, 89, 50, 37, 84, 63, 34, 32, 3, 17, 62, 40, 98, 82, 89, 24, 43, 85, 39, 15, 3, 99, 29, 20, 42, 27, 10, 85, 66, 50, 35, 69, 70, 70, 74, 30, 13, 72, 54, 11, 5, 70, 55, 91, 52, 10, 43, 43, 52)
            .map { it.toULong() }

        assertEquals(
            expected = expectedNumbers,
            actual = numbers
        )
    }

    @Test
    fun `Random Number Generator 2`() {
        val checksum: Long = crc32Checksum("Wolf".toByteArray())
        val checksumBytes = Ints.toByteArray(checksum.toInt())
        val rng = RandomXoshiro256StarStar(checksumBytes)
        val numbers = List(100) { rng.nextLong().toULong() % 100u }
        val expectedNumbers = listOf(88, 44, 94, 74, 0, 99, 7, 77, 68, 35, 47, 78, 19, 21, 50, 15, 42, 36, 91, 11, 85, 39, 64, 22, 57, 11, 25, 12, 1, 91, 17, 75, 29, 47, 88, 11, 68, 58, 27, 65, 21, 54, 47, 54, 73, 83, 23, 58, 75, 27, 26, 15, 60, 36, 30, 21, 55, 57, 77, 76, 75, 47, 53, 76, 9, 91, 14, 69, 3, 95, 11, 73, 20, 99, 68, 61, 3, 98, 36, 98, 56, 65, 14, 80, 74, 57, 63, 68, 51, 56, 24, 39, 53, 80, 57, 51, 81, 3, 1, 30)
            .map { it.toULong() }
        println(numbers)
        assertEquals(
            expected = expectedNumbers,
            actual = numbers
        )
    }

    @Test
    fun `Random Number Generator 3`() {
        val rng = RandomXoshiro256StarStar("Wolf")
        val numbers = List(100) { rng.nextInt(1, 10) }
        val expectedNumbers = listOf(6, 5, 8, 4, 10, 5, 7, 10, 4, 9, 10, 9, 7, 7, 1, 1, 2, 9, 9, 2, 6, 4, 5, 7, 8, 5, 4, 2, 3, 8, 7, 4, 5, 1, 10, 9, 3, 10, 2, 6, 8, 5, 7, 9, 3, 1, 5, 2, 7, 1, 4, 4, 4, 4, 9, 4, 5, 5, 6, 9, 5, 1, 2, 8, 3, 3, 2, 8, 4, 3, 2, 1, 10, 8, 9, 3, 10, 8, 5, 5, 6, 7, 10, 5, 8, 9, 4, 6, 4, 2, 10, 2, 1, 7, 9, 6, 7, 4, 2, 5)

        assertEquals(
            expected = expectedNumbers,
            actual = numbers
        )
    }

    @Test
    fun `Random sampler`() {
        val sampler = RandomSampler(listOf<Double>(1.0, 2.0, 4.0, 8.0))
        val rng = RandomXoshiro256StarStar("Wolf")
        val samples = List(500) { sampler.next(rng) }
        val expectedSamples = listOf(3, 3, 3, 3, 3, 3, 3, 0, 2, 3, 3, 3, 3, 1, 2, 2, 1, 3, 3, 2, 3, 3, 1, 1, 2, 1, 1, 3, 1, 3, 1, 2, 0, 2, 1, 0, 3, 3, 3, 1, 3, 3, 3, 3, 1, 3, 2, 3, 2, 2, 3, 3, 3, 3, 2, 3, 3, 0, 3, 3, 3, 3, 1, 2, 3, 3, 2, 2, 2, 1, 2, 2, 1, 2, 3, 1, 3, 0, 3, 2, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 1, 3, 3, 2, 0, 2, 2, 3, 1, 1, 2, 3, 2, 3, 3, 3, 3, 2, 3, 3, 3, 3, 3, 2, 3, 1, 2, 1, 1, 3, 1, 3, 2, 2, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 2, 3, 2, 3, 3, 1, 2, 3, 3, 1, 3, 2, 3, 3, 3, 2, 3, 1, 3, 0, 3, 2, 1, 1, 3, 1, 3, 2, 3, 3, 3, 3, 2, 0, 3, 3, 1, 3, 0, 2, 1, 3, 3, 1, 1, 3, 1, 2, 3, 3, 3, 0, 2, 3, 2, 0, 1, 3, 3, 3, 2, 2, 2, 3, 3, 3, 3, 3, 2, 3, 3, 3, 3, 2, 3, 3, 2, 0, 2, 3, 3, 3, 3, 2, 1, 1, 1, 2, 1, 3, 3, 3, 2, 2, 3, 3, 1, 2, 3, 0, 3, 2, 3, 3, 3, 3, 0, 2, 2, 3, 2, 2, 3, 3, 3, 3, 1, 3, 2, 3, 3, 3, 3, 3, 2, 2, 3, 1, 3, 0, 2, 1, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 2, 2, 2, 3, 1, 1, 3, 2, 2, 0, 3, 2, 1, 2, 1, 0, 3, 3, 3, 2, 2, 3, 2, 1, 2, 0, 0, 3, 3, 2, 3, 3, 2, 3, 3, 3, 3, 3, 2, 2, 2, 3, 3, 3, 3, 3, 1, 1, 3, 2, 2, 3, 1, 1, 0, 1, 3, 2, 3, 3, 2, 3, 3, 2, 3, 3, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 1, 2, 3, 3, 2, 2, 2, 2, 3, 3, 2, 0, 2, 1, 3, 3, 3, 3, 0, 3, 3, 3, 3, 2, 2, 3, 1, 3, 3, 3, 2, 3, 3, 3, 2, 3, 3, 3, 3, 2, 3, 2, 1, 3, 3, 3, 3, 2, 2, 0, 1, 2, 3, 2, 0, 3, 3, 3, 3, 3, 3, 1, 3, 3, 2, 3, 2, 2, 3, 3, 3, 3, 3, 2, 2, 3, 3, 2, 2, 2, 1, 3, 3, 3, 3, 1, 2, 3, 2, 3, 3, 2, 3, 2, 3, 3, 3, 2, 3, 1, 2, 3, 2, 1, 1, 3, 3, 2, 3, 3, 2, 3, 3, 0, 0, 1, 3, 3, 2, 3, 3, 3, 3, 1, 3, 3, 0, 3, 2, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 0, 3, 3, 2)

        assertEquals(
            expected = expectedSamples,
            actual = samples
        )

        // Check the distribution of the samples
        val totals = samples.groupingBy { it }.eachCount()
        val sortedValues = totals.entries.sortedBy { it.key }.map { it.value }

        println("Totals: $totals")
        println("Sorted Values: $sortedValues")

        assertEquals(listOf(28, 68, 130, 274), sortedValues)
        assertEquals(500, sortedValues.sum())
    }

    @Test
    fun `Fragment length 1`() {
        val fragmentLength = FountainEncoder.findNominalFragmentLength(
            messageLength = 12345,
            minimumFragmentLength = 1005,
            maximumFragmentLength = 1955
        )

        assertEquals(
            expected = 1764,
            actual = fragmentLength
        )
    }

    @Test
    fun `Fragment length 2`() {
        val fragmentLength = FountainEncoder.findNominalFragmentLength(
            messageLength = 12345,
            minimumFragmentLength = 1005,
            maximumFragmentLength = 30000
        )

        assertEquals(
            expected = 12345,
            actual = fragmentLength
        )
    }

    @Test
    fun `Encoder test`() {
        val message = makeMessage(1024, "Wolf")
        val fragmentLength = FountainEncoder.findNominalFragmentLength(message.size, 10, 100)
        val segments = FountainEncoder.partitionMessage(message, fragmentLength)
        val expectedFragments = listOf(
            "916ec65cf77cadf55cd7f9cda1a1030026ddd42e905b77adc36e4f2d3ccba44f7f04f2de44f42d84c374a0e149136f25b01852545961d55f7f7a8cde6d0e2ec43f3b2dcb644a2209e8c9e34af5c4747984a5e873c9cf5f965e25ee29039f",
            "df8ca74f1c769fc07eb7ebaec46e0695aea6cbd60b3ec4bbff1b9ffe8a9e7240129377b9d3711ed38d412fbb4442256f1e6f595e0fc57fed451fb0a0101fb76b1fb1e1b88cfdfdaa946294a47de8fff173f021c0e6f65b05c0a494e50791",
            "270a0050a73ae69b6725505a2ec8a5791457c9876dd34aadd192a53aa0dc66b556c0c215c7ceb8248b717c22951e65305b56a3706e3e86eb01c803bbf915d80edcd64d4d41977fa6f78dc07eecd072aae5bc8a852397e06034dba6a0b570",
            "797c3a89b16673c94838d884923b8186ee2db5c98407cab15e13678d072b43e406ad49477c2e45e85e52ca82a94f6df7bbbe7afbed3a3a830029f29090f25217e48d1f42993a640a67916aa7480177354cc7440215ae41e4d02eae9a1912",
            "33a6d4922a792c1b7244aa879fefdb4628dc8b0923568869a983b8c661ffab9b2ed2c149e38d41fba090b94155adbed32f8b18142ff0d7de4eeef2b04adf26f2456b46775c6c20b37602df7da179e2332feba8329bbb8d727a138b4ba7a5",
            "03215eda2ef1e953d89383a382c11d3f2cad37a4ee59a91236a3e56dcf89f6ac81dd4159989c317bd649d9cbc617f73fe10033bd288c60977481a09b343d3f676070e67da757b86de27bfca74392bac2996f7822a7d8f71a489ec6180390",
            "089ea80a8fcd6526413ec6c9a339115f111d78ef21d456660aa85f790910ffa2dc58d6a5b93705caef1091474938bd312427021ad1eeafbd19e0d916ddb111fabd8dcab5ad6a6ec3a9c6973809580cb2c164e26686b5b98cfb017a337968",
            "c7daaa14ae5152a067277b1b3902677d979f8e39cc2aafb3bc06fcf69160a853e6869dcc09a11b5009f91e6b89e5b927ab1527a735660faa6012b420dd926d940d742be6a64fb01cdc0cff9faa323f02ba41436871a0eab851e7f5782d10",
            "fbefde2a7e9ae9dc1e5c2c48f74f6c824ce9ef3c89f68800d44587bedc4ab417cfb3e7447d90e1e417e6e05d30e87239d3a5d1d45993d4461e60a0192831640aa32dedde185a371ded2ae15f8a93dba8809482ce49225daadfbb0fec629e",
            "23880789bdf9ed73be57fa84d555134630e8d0f7df48349f29869a477c13ccca9cd555ac42ad7f568416c3d61959d0ed568b2b81c7771e9088ad7fd55fd4386bafbf5a528c30f107139249357368ffa980de2c76ddd9ce4191376be0e6b5",
            "170010067e2e75ebe2d2904aeb1f89d5dc98cd4a6f2faaa8be6d03354c990fd895a97feb54668473e9d942bb99e196d897e8f1b01625cf48a7b78d249bb4985c065aa8cd1402ed2ba1b6f908f63dcd84b66425df00000000000000000000"
        )

        assertEquals(
            expected = expectedFragments,
            actual = segments.map { it.toHexString() }
        )

        val rejoinedMessage: ByteArray = FountainDecoder().joinFragments(segments, message.size)
        assertTrue(message.contentEquals(rejoinedMessage))
    }

    @Test
    fun `Degree chooser`() {
        val message = makeMessage(1024, "Wolf")
        val fragmentLength = FountainEncoder.findNominalFragmentLength(message.size, 10, 100)
        val fragments = FountainEncoder.partitionMessage(message, fragmentLength)
        val sequenceLength = fragments.size
        val rng = RandomXoshiro256StarStar("Wolf")
        val degrees = List(1000) { chooseDegree(sequenceLength, rng) }
        val expectedDegrees = listOf(7, 9, 2, 1, 4, 2, 1, 1, 3, 10, 7, 1, 1, 4, 3, 8, 6, 2, 3, 2, 1, 1, 4, 5, 8, 4, 4, 1, 6, 1, 5, 2, 3, 3, 5, 2, 1, 10, 2, 5, 1, 1, 1, 5, 5, 11, 1, 1, 8, 2, 1, 1, 2, 1, 1, 1, 1, 1, 1, 11, 1, 1, 5, 1, 1, 1, 3, 7, 3, 3, 2, 2, 4, 2, 1, 3, 1, 1, 8, 2, 1, 1, 2, 7, 1, 1, 2, 1, 2, 1, 4, 1, 1, 1, 2, 1, 8, 1, 5, 4, 2, 1, 1, 1, 1, 4, 1, 8, 1, 5, 4, 9, 1, 8, 6, 6, 7, 5, 4, 8, 5, 1, 2, 2, 11, 10, 1, 4, 3, 1, 2, 1, 2, 5, 1, 6, 2, 1, 3, 1, 8, 6, 3, 8, 1, 4, 1, 7, 6, 11, 1, 6, 1, 5, 5, 1, 3, 2, 4, 6, 3, 5, 1, 8, 1, 1, 1, 11, 3, 1, 2, 1, 4, 1, 2, 7, 5, 5, 5, 4, 6, 4, 3, 2, 3, 9, 1, 2, 3, 1, 2, 2, 5, 1, 1, 10, 3, 7, 2, 6, 1, 1, 1, 1, 3, 9, 1, 3, 1, 8, 4, 1, 3, 2, 3, 1, 1, 2, 4, 3, 4, 4, 4, 2, 6, 1, 7, 10, 3, 8, 1, 7, 6, 7, 1, 1, 1, 3, 11, 1, 1, 1, 2, 2, 3, 2, 8, 3, 1, 1, 2, 1, 3, 1, 3, 10, 1, 9, 11, 10, 3, 2, 5, 6, 1, 3, 3, 5, 1, 8, 8, 1, 2, 3, 1, 7, 6, 1, 11, 4, 9, 1, 1, 8, 1, 5, 3, 1, 8, 1, 1, 1, 3, 4, 2, 5, 1, 2, 10, 1, 8, 2, 11, 7, 4, 9, 2, 1, 1, 1, 3, 10, 1, 2, 1, 1, 8, 1, 1, 7, 1, 2, 9, 1, 1, 1, 11, 6, 6, 1, 8, 8, 4, 5, 6, 3, 5, 1, 7, 9, 1, 9, 2, 7, 8, 1, 1, 1, 2, 2, 2, 1, 8, 8, 2, 3, 2, 5, 8, 1, 5, 1, 3, 8, 8, 10, 2, 8, 3, 9, 3, 5, 2, 4, 2, 2, 2, 10, 6, 2, 2, 2, 11, 5, 4, 11, 1, 6, 10, 1, 10, 8, 1, 10, 6, 1, 2, 1, 3, 5, 1, 1, 4, 10, 2, 7, 2, 5, 8, 2, 2, 3, 11, 1, 6, 1, 6, 1, 4, 5, 1, 2, 5, 2, 1, 1, 2, 9, 2, 10, 1, 3, 1, 10, 3, 2, 7, 6, 1, 1, 4, 3, 6, 6, 1, 2, 1, 4, 2, 1, 2, 1, 1, 1, 3, 1, 4, 7, 11, 1, 4, 5, 2, 1, 2, 1, 9, 7, 1, 1, 2, 1, 6, 1, 1, 7, 11, 1, 1, 9, 5, 1, 1, 1, 4, 2, 1, 1, 4, 6, 2, 3, 1, 1, 1, 2, 1, 9, 1, 7, 1, 7, 1, 1, 1, 11, 1, 11, 11, 1, 8, 3, 5, 6, 4, 3, 9, 4, 1, 3, 1, 3, 2, 1, 1, 1, 1, 2, 1, 8, 1, 1, 6, 6, 3, 1, 8, 7, 2, 1, 2, 7, 6, 4, 3, 6, 1, 6, 3, 3, 2, 9, 9, 5, 2, 1, 2, 1, 9, 8, 8, 3, 7, 1, 5, 1, 2, 3, 1, 5, 2, 7, 8, 5, 1, 1, 2, 1, 1, 4, 3, 3, 2, 6, 2, 2, 1, 3, 4, 1, 2, 8, 2, 1, 4, 1, 2, 1, 2, 4, 1, 3, 1, 1, 1, 10, 1, 1, 2, 5, 11, 4, 1, 1, 1, 4, 3, 7, 1, 6, 8, 1, 3, 5, 1, 4, 1, 7, 8, 1, 4, 1, 2, 2, 7, 3, 1, 9, 11, 7, 1, 9, 4, 5, 2, 1, 5, 2, 4, 5, 1, 4, 2, 5, 2, 1, 10, 2, 1, 7, 4, 1, 7, 11, 5, 2, 11, 7, 6, 2, 1, 11, 3, 1, 5, 1, 1, 4, 10, 4, 1, 2, 1, 4, 11, 3, 1, 1, 1, 7, 1, 3, 1, 1, 7, 10, 6, 3, 6, 3, 9, 1, 3, 4, 7, 4, 1, 1, 1, 5, 7, 4, 5, 1, 6, 1, 4, 4, 8, 9, 1, 1, 2, 1, 10, 3, 1, 2, 1, 2, 3, 6, 2, 9, 1, 1, 6, 2, 3, 5, 2, 10, 5, 4, 10, 5, 2, 1, 5, 2, 1, 4, 4, 1, 2, 1, 1, 1, 9, 3, 3, 4, 2, 6, 7, 1, 1, 8, 3, 11, 1, 1, 2, 3, 8, 7, 11, 1, 1, 9, 3, 2, 2, 9, 3, 1, 8, 3, 7, 2, 4, 4, 1, 1, 5, 1, 1, 1, 2, 3, 10, 1, 11, 5, 3, 1, 1, 7, 9, 1, 1, 3, 5, 7, 5, 1, 5, 1, 2, 1, 11, 2, 1, 3, 3, 1, 1, 1, 2, 7, 9, 9, 5, 1, 4, 3, 5, 5, 8, 2, 1, 1, 2, 1, 2, 5, 4, 3, 3, 2, 4, 2, 4, 1, 8, 1, 2, 8, 3, 1, 8, 1, 1, 3, 2, 1, 1, 7, 1, 8, 1, 1, 1, 1, 2, 3, 6, 7, 1, 4, 4, 9, 6, 3, 4, 7, 6, 10, 1, 5, 6, 2, 3, 2, 3, 2, 11, 5, 3, 3, 6, 2, 1, 8, 5, 1, 8, 7, 2, 10, 1, 3, 1, 9, 2, 1, 10, 3, 3, 1, 1, 1, 1, 1, 8, 7, 3, 3, 1, 3, 4, 2, 8, 5, 6, 1, 10, 7, 4, 8, 1, 1, 1, 2, 3, 10, 2, 3, 3, 5, 3, 2, 3, 3, 5, 2, 2, 7, 1, 2, 6, 1, 1, 6, 1, 8, 7, 5, 10, 3, 9, 6, 3, 3, 11, 10, 4, 10, 5, 2, 1, 4, 1, 2, 6, 6, 3, 4, 1, 1, 2, 2, 1, 2, 1, 1, 3, 1, 1, 3)

        assertEquals(
            expected = expectedDegrees,
            actual = degrees
        )

        val totals = degrees.groupingBy { it }.eachCount()
        val sortedValues = totals.entries.sortedBy { it.key }.map { it.value }
        val expectedSortedDegrees = listOf(328, 151, 116, 77, 71, 54, 52, 55, 33, 33, 30)
        assertEquals(
            expected = expectedSortedDegrees,
            actual = sortedValues
        )
    }

    @Test
    fun `Fisher-Yates Shuffle`() {
        val rng = RandomXoshiro256StarStar("Wolf")
        val indexes: List<Int> = (1..10).toList()

        val result = indexes.map { count ->
            shuffle(indexes, rng, count)
        }.toList()

        val expectedResult = listOf(
            listOf(6),
            listOf(5, 8),
            listOf(4, 10, 5),
            listOf(7, 10, 3, 8),
            listOf(10, 8, 6, 5, 1),
            listOf(1, 3, 9, 8, 4, 6),
            listOf(4, 6, 8, 9, 3, 2, 1),
            listOf(3, 9, 7, 4, 5, 1, 10, 8),
            listOf(3, 10, 2, 6, 8, 5, 7, 9, 1),
            listOf(1, 5, 3, 8, 2, 6, 7, 9, 4, 10),
        )

        assertEquals(
            expected = expectedResult,
            actual = result
        )
    }

    // This test is not in the implementation guide but is the better version of the test in it.
    @Test
    fun `Fisher-Yates Shuffle more explicit test`() {
        val indexes: List<Int> = (1..10).toList()

        val result = indexes.map { count ->
            val rng = RandomXoshiro256StarStar("Wolf")
            shuffle(indexes, rng, count)
        }.toList()

        val expectedResult = listOf(
            listOf(6),
            listOf(6, 4),
            listOf(6, 4, 9),
            listOf(6, 4, 9, 3),
            listOf(6, 4, 9, 3, 10),
            listOf(6, 4, 9, 3, 10, 5),
            listOf(6, 4, 9, 3, 10, 5, 7),
            listOf(6, 4, 9, 3, 10, 5, 7, 8),
            listOf(6, 4, 9, 3, 10, 5, 7, 8, 1),
            listOf(6, 4, 9, 3, 10, 5, 7, 8, 1, 2),
        )

        assertEquals(
            expected = expectedResult,
            actual = result
        )
    }

    @Test
    fun `Fragment chooser`() {
        val message: ByteArray = makeMessage(1024, "Wolf")
        val checksum: Long = crc32Checksum(message)
        val fragmentLength = FountainEncoder.findNominalFragmentLength(message.size, 10, 100)
        val fragments = FountainEncoder.partitionMessage(message, fragmentLength)
        val fragmentIndexes = (1..50).map { number ->
            chooseFragments(number, fragments.size, checksum).sorted()
        }.toList()

        val expectedFragmentIndexes = listOf(
            // Fixed-rate parts
            listOf(0),
            listOf(1),
            listOf(2),
            listOf(3),
            listOf(4),
            listOf(5),
            listOf(6),
            listOf(7),
            listOf(8),
            listOf(9),
            listOf(10),

            // Rateless parts
            listOf(9),
            listOf(2, 5, 6, 8, 9, 10),
            listOf(8),
            listOf(1, 5),
            listOf(1),
            listOf(0, 2, 4, 5, 8, 10),
            listOf(5),
            listOf(2),
            listOf(2),
            listOf(0, 1, 3, 4, 5, 7, 9, 10),
            listOf(0, 1, 2, 3, 5, 6, 8, 9, 10),
            listOf(0, 2, 4, 5, 7, 8, 9, 10),
            listOf(3, 5),
            listOf(4),
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            listOf(0, 1, 3, 4, 5, 6, 7, 9, 10),
            listOf(6),
            listOf(5, 6),
            listOf(7),
            listOf(4, 9, 10),
            listOf(5),
            listOf(10),
            listOf(1, 3, 4, 5),
            listOf(6, 8),
            listOf(9),
            listOf(4, 5, 6, 8),
            listOf(4),
            listOf(0, 10),
            listOf(2, 5, 7, 10),
            listOf(4),
            listOf(0, 2, 4, 6, 7, 10),
            listOf(9),
            listOf(1),
            listOf(3, 6),
            listOf(3, 8),
            listOf(1, 2, 6, 9),
            listOf(0, 2, 4, 5, 6, 7, 9),
            listOf(0, 4),
            listOf(9),
        )

        assertEquals(
            expected = expectedFragmentIndexes,
            actual = fragmentIndexes
        )
    }

    // The Swift implementation uses a UInt for the sequenceNumber, but if I switch the Kotlin version
    // to use a UInt, the CBOR encoding is different. Investigate why. I'm going to leave it as a Long for now.
    // This matches the Hummingbird implementation.
    // @Test
    // fun `CBOR encoder`() {
    //     val part: FountainEncoder.Part = FountainEncoder.Part(
    //         sequenceNumber = 12,
    //         sequenceLength = 8,
    //         messageLength = 100,
    //         checksum = 0x12345678,
    //         data = byteArrayOf(0x01, 0x05, 0x03, 0x03, 0x05)
    //     )
    //     val cbor = part.toCborBytes()
    //
    //     println("CBOR: ${cbor.toHexString()}")
    //
    //     val expectedCbor = "850c0818641a12345678450105030305"
    //
    //     // TODO: Clean up this test
    //     assertEquals(
    //         expected = expectedCbor,
    //         actual = cbor.toHexString()
    //     )
    //
    //     val decodedPart = FountainEncoder.Part.fromCborBytes(cbor)
    //
    //     assertEquals(
    //         expected = cbor.toHexString(),
    //         actual = decodedPart.toCborBytes().toHexString()
    //     )
    // }

    @Test
    fun XOR() {
        val rng = RandomXoshiro256StarStar("Wolf")
        val data1 = ByteArray(10)
        rng.nextData(data1)

        assertEquals(
            expected = "916ec65cf77cadf55cd7",
            actual = data1.toHexString()
        )

        val data2 = ByteArray(10)
        rng.nextData(data2)

        assertEquals(
            expected = "f9cda1a1030026ddd42e",
            actual = data2.toHexString()
        )

        val xor = FountainEncoder.xor(data1, data2)
        assertEquals(
            expected = "68a367fdf47c8b2888f9",
            actual = xor.toHexString()
        )
    }

    @Test
    fun `Test encoder`() {
        val message = makeMessage(256, "Wolf")
        val encoder = FountainEncoder(message, maximumFragmentLen = 30)
        val parts = (1..20).map { encoder.nextPart().toString() }.toList()

        val expectedParts = listOf(
            "Part(sequenceNumber=1, sequenceLength=9, messageLength=256, checksum=23570951, data=916ec65cf77cadf55cd7f9cda1a1030026ddd42e905b77adc36e4f2d3c)",
            "Part(sequenceNumber=2, sequenceLength=9, messageLength=256, checksum=23570951, data=cba44f7f04f2de44f42d84c374a0e149136f25b01852545961d55f7f7a)",
            "Part(sequenceNumber=3, sequenceLength=9, messageLength=256, checksum=23570951, data=8cde6d0e2ec43f3b2dcb644a2209e8c9e34af5c4747984a5e873c9cf5f)",
            "Part(sequenceNumber=4, sequenceLength=9, messageLength=256, checksum=23570951, data=965e25ee29039fdf8ca74f1c769fc07eb7ebaec46e0695aea6cbd60b3e)",
            "Part(sequenceNumber=5, sequenceLength=9, messageLength=256, checksum=23570951, data=c4bbff1b9ffe8a9e7240129377b9d3711ed38d412fbb4442256f1e6f59)",
            "Part(sequenceNumber=6, sequenceLength=9, messageLength=256, checksum=23570951, data=5e0fc57fed451fb0a0101fb76b1fb1e1b88cfdfdaa946294a47de8fff1)",
            "Part(sequenceNumber=7, sequenceLength=9, messageLength=256, checksum=23570951, data=73f021c0e6f65b05c0a494e50791270a0050a73ae69b6725505a2ec8a5)",
            "Part(sequenceNumber=8, sequenceLength=9, messageLength=256, checksum=23570951, data=791457c9876dd34aadd192a53aa0dc66b556c0c215c7ceb8248b717c22)",
            "Part(sequenceNumber=9, sequenceLength=9, messageLength=256, checksum=23570951, data=951e65305b56a3706e3e86eb01c803bbf915d80edcd64d4d0000000000)",
            "Part(sequenceNumber=10, sequenceLength=9, messageLength=256, checksum=23570951, data=330f0f33a05eead4f331df229871bee733b50de71afd2e5a79f196de09)",
            "Part(sequenceNumber=11, sequenceLength=9, messageLength=256, checksum=23570951, data=3b205ce5e52d8c24a52cffa34c564fa1af3fdffcd349dc4258ee4ee828)",
            "Part(sequenceNumber=12, sequenceLength=9, messageLength=256, checksum=23570951, data=dd7bf725ea6c16d531b5f03254783803048ca08b87148daacd1cd7a006)",
            "Part(sequenceNumber=13, sequenceLength=9, messageLength=256, checksum=23570951, data=760be7ad1c6187902bbc04f539b9ee5eb8ea6833222edea36031306c01)",
            "Part(sequenceNumber=14, sequenceLength=9, messageLength=256, checksum=23570951, data=5bf4031217d2c3254b088fa7553778b5003632f46e21db129416f65b55)",
            "Part(sequenceNumber=15, sequenceLength=9, messageLength=256, checksum=23570951, data=73f021c0e6f65b05c0a494e50791270a0050a73ae69b6725505a2ec8a5)",
            "Part(sequenceNumber=16, sequenceLength=9, messageLength=256, checksum=23570951, data=b8546ebfe2048541348910267331c643133f828afec9337c318f71b7df)",
            "Part(sequenceNumber=17, sequenceLength=9, messageLength=256, checksum=23570951, data=23dedeea74e3a0fb052befabefa13e2f80e4315c9dceed4c8630612e64)",
            "Part(sequenceNumber=18, sequenceLength=9, messageLength=256, checksum=23570951, data=d01a8daee769ce34b6b35d3ca0005302724abddae405bdb419c0a6b208)",
            "Part(sequenceNumber=19, sequenceLength=9, messageLength=256, checksum=23570951, data=3171c5dc365766eff25ae47c6f10e7de48cfb8474e050e5fe997a6dc24)",
            "Part(sequenceNumber=20, sequenceLength=9, messageLength=256, checksum=23570951, data=e055c2433562184fa71b4be94f262e200f01c6f74c284b0dc6fae6673f)"
        )

        assertEquals(
            expected = expectedParts,
            actual = parts
        )
    }

    @Test
    fun `Test encoder CBOR`() {
        val message = makeMessage(256, "Wolf")
        val encoder = FountainEncoder(message, maximumFragmentLen = 30)
        val parts = (1..20).map { encoder.nextPart().toCborBytes().toHexString() }.toList()

        val expectedParts = listOf(
            "8501091901001a0167aa07581d916ec65cf77cadf55cd7f9cda1a1030026ddd42e905b77adc36e4f2d3c",
            "8502091901001a0167aa07581dcba44f7f04f2de44f42d84c374a0e149136f25b01852545961d55f7f7a",
            "8503091901001a0167aa07581d8cde6d0e2ec43f3b2dcb644a2209e8c9e34af5c4747984a5e873c9cf5f",
            "8504091901001a0167aa07581d965e25ee29039fdf8ca74f1c769fc07eb7ebaec46e0695aea6cbd60b3e",
            "8505091901001a0167aa07581dc4bbff1b9ffe8a9e7240129377b9d3711ed38d412fbb4442256f1e6f59",
            "8506091901001a0167aa07581d5e0fc57fed451fb0a0101fb76b1fb1e1b88cfdfdaa946294a47de8fff1",
            "8507091901001a0167aa07581d73f021c0e6f65b05c0a494e50791270a0050a73ae69b6725505a2ec8a5",
            "8508091901001a0167aa07581d791457c9876dd34aadd192a53aa0dc66b556c0c215c7ceb8248b717c22",
            "8509091901001a0167aa07581d951e65305b56a3706e3e86eb01c803bbf915d80edcd64d4d0000000000",
            "850a091901001a0167aa07581d330f0f33a05eead4f331df229871bee733b50de71afd2e5a79f196de09",
            "850b091901001a0167aa07581d3b205ce5e52d8c24a52cffa34c564fa1af3fdffcd349dc4258ee4ee828",
            "850c091901001a0167aa07581ddd7bf725ea6c16d531b5f03254783803048ca08b87148daacd1cd7a006",
            "850d091901001a0167aa07581d760be7ad1c6187902bbc04f539b9ee5eb8ea6833222edea36031306c01",
            "850e091901001a0167aa07581d5bf4031217d2c3254b088fa7553778b5003632f46e21db129416f65b55",
            "850f091901001a0167aa07581d73f021c0e6f65b05c0a494e50791270a0050a73ae69b6725505a2ec8a5",
            "8510091901001a0167aa07581db8546ebfe2048541348910267331c643133f828afec9337c318f71b7df",
            "8511091901001a0167aa07581d23dedeea74e3a0fb052befabefa13e2f80e4315c9dceed4c8630612e64",
            "8512091901001a0167aa07581dd01a8daee769ce34b6b35d3ca0005302724abddae405bdb419c0a6b208",
            "8513091901001a0167aa07581d3171c5dc365766eff25ae47c6f10e7de48cfb8474e050e5fe997a6dc24",
            "8514091901001a0167aa07581de055c2433562184fa71b4be94f262e200f01c6f74c284b0dc6fae6673f"
        )

        assertEquals(
            expected = expectedParts,
            actual = parts
        )
    }

    @Test
    fun `Test encoder is complete`() {
        val message = makeMessage(256, "Wolf")
        val encoder = FountainEncoder(message, maximumFragmentLen = 30)
        var generatedPartsCount = 0

        while (!encoder.isComplete()) {
            encoder.nextPart()
            generatedPartsCount++
        }

        assertEquals(
            expected = encoder.sequenceLength,
            actual = generatedPartsCount
        )
    }

    @Test
    fun `Test decoder`() {
        val message = makeMessage(32767, "Wolf")
        println("Message is ${message.toHexString()}")

        val encoder = FountainEncoder(message, maximumFragmentLen = 1000)
        val decoder = FountainDecoder()
        println("Decoder result at this point is ${decoder.result}")
        while (decoder.result == null) {
            val part = encoder.nextPart()
            decoder.receivePart(part)
        }
        if (decoder.result!!.isSuccess) {
            println(decoder.result!!.getOrThrow().toHexString())
            assertEquals(
                expected = message.toHexString(),
                actual = decoder.result!!.getOrThrow().toHexString()
            )
        } else {
            decoder.result!!.getOrThrow()
        }
    }
}
