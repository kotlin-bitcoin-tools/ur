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

    @Test
    fun `Bigger input`() {
        val bytes = listOf(
            245, 215, 20, 198, 241, 235, 69, 59, 209, 205, 165, 18, 150, 158, 116, 135, 229, 212, 19, 159,
            17, 37, 239, 240, 253, 11, 109, 191, 37, 242, 38, 120, 223, 41, 156, 189, 242, 254, 147, 204,
            66, 163, 216, 175, 191, 72, 169, 54, 32, 60, 144, 230, 210, 137, 184, 197, 33, 113, 88, 14,
            157, 31, 177, 46, 1, 115, 205, 69, 225, 150, 65, 235, 58, 144, 65, 240, 133, 69, 113, 247,
            63, 53, 242, 165, 160, 144, 26, 13, 79, 237, 133, 71, 82, 69, 254, 165, 138, 41, 85, 24
        )
        val input = bytes.map { it.toByte() }.toByteArray()
        val expectedEncoded = "yank toys bulb skew when warm free fair tent swan open brag mint noon jury list view tiny brew note body data webs what zinc bald join runs data whiz days keys user diet news ruby whiz zone menu surf flew omit trip pose runs fund part even crux fern math visa tied loud redo silk curl jugs hard beta next cost puma drum acid junk swan free very mint flap warm fact math flap what limp free jugs yell fish epic whiz open numb math city belt glow wave limp fuel grim free zone open love diet gyro cats fizz holy city puff"
        val expectedEncodedMinimal = "yktsbbswwnwmfefrttsnonbgmtnnjyltvwtybwnebydawswtzcbdjnrsdawzdsksurdtnsrywzzemusffwottppersfdptencxfnmhvatdldroskcljshdbantctpadmadjksnfevymtfpwmftmhfpwtlpfejsylfhecwzonnbmhcybtgwwelpflgmfezeonledtgocsfzhycypf"

        assertEquals(
            expected = expectedEncoded,
            actual = Bytewords.encode(input, Bytewords.Style.STANDARD)
        )
        assertEquals(
            expected = expectedEncodedMinimal,
            actual = Bytewords.encode(input, Bytewords.Style.MINIMAL)
        )
    }
}
