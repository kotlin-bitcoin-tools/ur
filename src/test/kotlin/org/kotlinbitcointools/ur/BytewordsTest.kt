package org.kotlinbitcointools.ur

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Taken from URKit's BytewordsTests.swift
// https://github.com/BlockchainCommons/URKit/blob/master/Tests/URKitTests/BytewordsTests.swift
class BytewordsTest {
    @Test
    fun `Encoding bytewords`() {
        val input = byteArrayOf(0x00, 0x01, 0x02, 0x80.toByte(), 0xff.toByte())

        assertEquals(
            expected = "able acid also lava zoom jade need echo taxi",
            actual = Bytewords.encode(input, Bytewords.Style.STANDARD)
        )
        assertEquals(
            expected = "able-acid-also-lava-zoom-jade-need-echo-taxi",
            actual = Bytewords.encode(input, Bytewords.Style.URI)
        )
        assertEquals(
            expected = "aeadaolazmjendeoti",
            actual = Bytewords.encode(input, Bytewords.Style.MINIMAL)
        )
    }

    @Test
    fun `Decoding bytewords`() {
        val input = byteArrayOf(0x00, 0x01, 0x02, 0x80.toByte(), 0xff.toByte())

        assertTrue(
            input.contentEquals(
                Bytewords.decode("able acid also lava zoom jade need echo taxi", Bytewords.Style.STANDARD)
            )
        )
        assertTrue(
            input.contentEquals(
                Bytewords.decode("able-acid-also-lava-zoom-jade-need-echo-taxi", Bytewords.Style.URI)
            )
        )
        assertTrue(
            input.contentEquals(
                Bytewords.decode("aeadaolazmjendeoti", Bytewords.Style.MINIMAL)
            )
        )
    }

    @Test
    fun `Incorrect checksum throws`() {
        assertFailsWith<InvalidChecksumException> {
            Bytewords.decode("able acid also lava zero jade need echo wolf", Bytewords.Style.STANDARD)
        }
        assertFailsWith<InvalidChecksumException> {
            Bytewords.decode("able-acid-also-lava-zero-jade-need-echo-wolf", Bytewords.Style.URI)
        }
        assertFailsWith<InvalidChecksumException> {
            Bytewords.decode("aeadaolazojendeowf", Bytewords.Style.MINIMAL)
        }
    }

    @Test
    fun `Message is too short throws`() {
        assertFailsWith<InvalidChecksumException> {
            Bytewords.decode("wolf", Bytewords.Style.STANDARD)
        }
    }

    @Test
    fun `Invalid word throws`() {
        assertFailsWith<InvalidBytewordException> {
            Bytewords.decode("mango", Bytewords.Style.STANDARD)
        }
        assertFailsWith<InvalidBytewordException> {
            Bytewords.decode("ma", Bytewords.Style.MINIMAL)
        }
    }
}
