/*
 * Copyright 2023-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur

import org.kotlinbitcointools.ur.utilities.crc32Checksum
import java.nio.ByteBuffer
import java.util.zip.CRC32

public class Bytewords {
    public enum class Style {
        STANDARD,
        URI,
        MINIMAL,
    }

    public companion object {
        public fun encode(data: ByteArray, style: Style): String {
            return when (style) {
                Style.STANDARD -> encode(data, " ")
                Style.URI      -> encode(data, "-")
                Style.MINIMAL  -> encodeMinimal(data)
            }
        }

        public fun decode(encoded: String, style: Style): ByteArray {
            return when (style) {
                Style.STANDARD -> decode(encoded, " ")
                Style.URI      -> decode(encoded, "-")
                Style.MINIMAL  -> decodeMinimal(encoded)
            }
        }

        private fun encode(data: ByteArray, separator: String): String {
            val dataAndChecksum = appendChecksum(data)
            return dataAndChecksum.joinToString(separator) { byte ->
                byteToWord(byte)
            }
        }

        private fun encodeMinimal(data: ByteArray): String {
            val dataAndChecksum = appendChecksum(data)
            return dataAndChecksum.joinToString("") { byte ->
                byteToMinimalWord(byte)
            }
        }

        private fun decode(encoded: String, separator: String): ByteArray {
            val words: List<String> = encoded.split(separator)
            val bytes: List<Byte> = words.map { word ->
                println("Looking for word: $word")
                wordToByte(word)
            }
            val dataAndChecksum: ByteArray = bytes.toByteArray()
            return stripChecksum(dataAndChecksum)
        }

        private fun decodeMinimal(encoded: String): ByteArray {
            val words: List<String> = encoded.chunked(2)
            val bytes: List<Byte> = words.map { word ->
                minimalWordToByte(word)
            }
            val dataAndChecksum: ByteArray = bytes.toByteArray()
            return stripChecksum(dataAndChecksum)
        }

        private fun appendChecksum(data: ByteArray): ByteArray {
            val crc32 = CRC32()
            crc32.update(data)

            val checksum = ByteBuffer.allocate(Long.SIZE_BYTES)
                .putLong(crc32.value)
                .array()
                .takeLast(4)
                .toByteArray()

            return data + checksum
        }

        private fun stripChecksum(dataAndChecksum: ByteArray): ByteArray {
            val checksumSize = 4
            val messageSize = dataAndChecksum.size - checksumSize
            if (checksumSize > dataAndChecksum.size) throw InvalidChecksumException("Data is too short to contain a checksum")

            val message = dataAndChecksum.sliceArray(0..<messageSize)
            val checksum = dataAndChecksum.sliceArray(messageSize..<dataAndChecksum.size)
            val computedChecksum = ByteBuffer.allocate(Long.SIZE_BYTES)
                .putLong(crc32Checksum(message))
                .array()
                .takeLast(4)
                .toByteArray()

            if (!checksum.contentEquals(computedChecksum)) throw InvalidChecksumException("Checksum does not match")

            return message
        }

        private fun byteToWord(byte: Byte): String {
            return bytewordsList[byte.toInt() and 0xFF]
        }

        private fun byteToMinimalWord(byte: Byte): String {
            return minimalBytewordsList[byte.toInt() and 0xFF]
        }

        // The indexOf() method returns -1 if the element is not found in the list
        private fun wordToByte(word: String): Byte {
            return bytewordsList.indexOf(word).let { index ->
                if (index == -1) throw InvalidBytewordException("Word not found in bytewords list: $word")
                index.toByte()
            }
        }

        private fun minimalWordToByte(word: String): Byte {
            return minimalBytewordsList.indexOf(word).let { index ->
                if (index == -1) throw InvalidBytewordException("Word not found in bytewords list: $word")
                index.toByte()
            }
        }
    }

}

private val bytewordsList: List<String> = listOf(
    "able", "acid", "also", "apex", "aqua", "arch", "atom", "aunt",
    "away", "axis", "back", "bald", "barn", "belt", "beta", "bias",
    "blue", "body", "brag", "brew", "bulb", "buzz", "calm", "cash",
    "cats", "chef", "city", "claw", "code", "cola", "cook", "cost",
    "crux", "curl", "cusp", "cyan", "dark", "data", "days", "deli",
    "dice" ,"diet" ,"door" ,"down" ,"draw" ,"drop" ,"drum" ,"dull",
    "duty", "each", "easy", "echo", "edge", "epic", "even", "exam",
    "exit", "eyes", "fact", "fair", "fern", "figs", "film", "fish",
    "fizz", "flap", "flew", "flux", "foxy", "free", "frog", "fuel",
    "fund", "gala", "game", "gear", "gems", "gift", "girl", "glow",
    "good", "gray", "grim", "guru", "gush", "gyro", "half", "hang",
    "hard", "hawk", "heat", "help", "high", "hill", "holy", "hope",
    "horn", "huts", "iced", "idea", "idle", "inch", "inky", "into",
    "iris", "iron", "item", "jade", "jazz", "join", "jolt", "jowl",
    "judo", "jugs", "jump", "junk", "jury", "keep", "keno", "kept",
    "keys", "kick", "kiln", "king", "kite", "kiwi", "knob", "lamb",
    "lava", "lazy", "leaf", "legs", "liar", "limp", "lion", "list",
    "logo", "loud", "love", "luau", "luck", "lung", "main", "many",
    "math", "maze", "memo", "menu", "meow", "mild", "mint", "miss",
    "monk", "nail", "navy", "need", "news", "next", "noon", "note",
    "numb", "obey", "oboe", "omit", "onyx", "open", "oval", "owls",
    "paid", "part", "peck", "play", "plus", "poem", "pool", "pose",
    "puff", "puma", "purr", "quad", "quiz", "race", "ramp", "real",
    "redo", "rich", "road", "rock", "roof", "ruby", "ruin", "runs",
    "rust", "safe", "saga", "scar", "sets", "silk", "skew", "slot",
    "soap", "solo", "song", "stub", "surf", "swan", "taco", "task",
    "taxi", "tent", "tied", "time", "tiny", "toil", "tomb", "toys",
    "trip", "tuna", "twin", "ugly", "undo", "unit", "urge", "user",
    "vast", "very", "veto", "vial", "vibe", "view", "visa", "void",
    "vows", "wall", "wand", "warm", "wasp", "wave", "waxy", "webs",
    "what", "when", "whiz", "wolf", "work", "yank", "yawn", "yell",
    "yoga", "yurt", "zaps", "zero", "zest", "zinc", "zone", "zoom"
)

private val minimalBytewordsList: List<String> = listOf(
    "ae", "ad", "ao", "ax", "aa", "ah", "am", "at",
    "ay", "as", "bk", "bd", "bn", "bt", "ba", "bs",
    "be", "by", "bg", "bw", "bb", "bz", "cm", "ch",
    "cs", "cf", "cy", "cw", "ce", "ca", "ck", "ct",
    "cx", "cl", "cp", "cn", "dk", "da", "ds", "di",
    "de" ,"dt" ,"dr" ,"dn" ,"dw" ,"dp" ,"dm" ,"dl",
    "dy", "eh", "ey", "eo", "ee", "ec", "en", "em",
    "et", "es", "ft", "fr", "fn", "fs", "fm", "fh",
    "fz", "fp", "fw", "fx", "fy", "fe", "fg", "fl",
    "fd", "ga", "ge", "gr", "gs", "gt", "gl", "gw",
    "gd", "gy", "gm", "gu", "gh", "go", "hf", "hg",
    "hd", "hk", "ht", "hp", "hh", "hl", "hy", "he",
    "hn", "hs", "id", "ia", "ie", "ih", "iy", "io",
    "is", "in", "im", "je", "jz", "jn", "jt", "jl",
    "jo", "js", "jp", "jk", "jy", "kp", "ko", "kt",
    "ks", "kk", "kn", "kg", "ke", "ki", "kb", "lb",
    "la", "ly", "lf", "ls", "lr", "lp", "ln", "lt",
    "lo", "ld", "le", "lu", "lk", "lg", "mn", "my",
    "mh", "me", "mo", "mu", "mw", "md", "mt", "ms",
    "mk", "nl", "ny", "nd", "ns", "nt", "nn", "ne",
    "nb", "oy", "oe", "ot", "ox", "on", "ol", "os",
    "pd", "pt", "pk", "py", "ps", "pm", "pl", "pe",
    "pf", "pa", "pr", "qd", "qz", "re", "rp", "rl",
    "ro", "rh", "rd", "rk", "rf", "ry", "rn", "rs",
    "rt", "se", "sa", "sr", "ss", "sk", "sw", "st",
    "sp", "so", "sg", "sb", "sf", "sn", "to", "tk",
    "ti", "tt", "td", "te", "ty", "tl", "tb", "ts",
    "tp", "ta", "tn", "uy", "uo", "ut", "ue", "ur",
    "vt", "vy", "vo", "vl", "ve", "vw", "va", "vd",
    "vs", "wl", "wd", "wm", "wp", "we", "wy", "ws",
    "wt", "wn", "wz", "wf", "wk", "yk", "yn", "yl",
    "ya", "yt", "zs", "zo", "zt", "zc", "ze", "zm"
)
