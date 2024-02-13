/*
 * Copyright 2023-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur

import org.kotlinbitcointools.ur.fountain.FountainDecoder
import org.kotlinbitcointools.ur.fountain.FountainEncoder
import org.kotlinbitcointools.ur.registry.RegistryType

public class URDecoder {
    public var result: Result<UR>? = null
    private var expectedType: RegistryType? = null
    private val fountainDecoder: FountainDecoder = FountainDecoder()

    public fun receivePart(string: String): Boolean {
        try {
            // Don't process the part if we're already done
            if (this.result != null) return false

            // Don't continue if this part is not valid
            val (type, body) = parse(string)
            if (!isCorrectType(type)) return false

            // If this is a single-part UR, we're done
            if (body.size == 1) {
                val body: String = body.first()
                val ur = decode(type, body)
                result = Result.success(ur)
            }

            // Multi-part URs must have two body components: seq/fragment
            if (body.size != 2) throw InvalidPathLengthException("Invalid UR path length of ${body.size}")
            val (sequence, fragment) = body

            // Parse the sequence component and the fragment, and make sure they agree.
            val sequenceComponentPattern = """(\d+)-(\d+)""".toRegex()
            val (sequenceNumberString, sequenceLengthString) = sequenceComponentPattern
                .matchEntire(sequence)?.destructured ?: throw InvalidSequenceComponentException("Invalid UR sequence component '$sequence'")
            val sequenceNumber = sequenceNumberString.toInt()
            val sequenceLength = sequenceLengthString.toInt()
            val cbor: ByteArray = Bytewords.decode(fragment, Bytewords.Style.MINIMAL)
            val part: FountainEncoder.Part = FountainEncoder.Part.fromCborBytes(cbor)

            if (sequenceNumber != part.sequenceNumber || sequenceLength != part.sequenceLength) {
                return false
            }

            // Process the part
            if (!fountainDecoder.receivePart(part)) return false

            // TODO: Deal with these non-null assertions more gracefully.
            if (fountainDecoder.result == null) {
                // Not done yet
            } else if (fountainDecoder.result!!.isSuccess) {
                val ur = UR(type, fountainDecoder.result!!.getOrThrow())
                result = Result.success(ur)
            } else {
                result = fountainDecoder.result!!.exceptionOrNull()?.let { Result.failure(it) }
            }

            return true
        } catch (e: URException) {
            // TODO: Wouldn't it be better to let the exception bubble up here instead of catching and returning a Boolean?
            return false
        }
    }

    private fun decode(type: RegistryType, body: String): UR {
        val cbor: ByteArray = Bytewords.decode(body, Bytewords.Style.MINIMAL)
        return UR(type, cbor)
    }

    private fun parse(part: String): Pair<RegistryType, List<String>> {
        // Don't consider case
        val lowercased: String = part.lowercase()

        // Validate URI scheme
        if (!lowercased.startsWith("ur:")) throw InvalidSchemeException("Invalid UR scheme '${lowercased.take(3)}'")

        // Split the remainder into path components
        val fullPath: String = lowercased.drop(3)
        val pathComponents: List<String> = fullPath.split("/")

        // Make sure there are at least two path components
        if (pathComponents.size < 2) throw InvalidPathLengthException("Invalid UR path length of ${pathComponents.size}")

        // Make sure the first path component is a valid registry type
        val type: RegistryType = RegistryType.fromString(pathComponents.first())

        // Remove the type from the path components to pull only the body
        val body: List<String> = pathComponents.drop(1)

        return Pair(type, body)
    }

    private fun isCorrectType(type: RegistryType): Boolean {
        if (expectedType == null) {
            expectedType = type
            return true
        } else {
            return type == expectedType
        }
    }

    private fun splitBody(body: List<String>): Pair<String, String> {
        val sequence: String = body.first()
        val fragment: String = body.last()
        return Pair(sequence, fragment)
    }

    // TODO: Why not only support the current `RegistryType`s? Why allow for custom types if we don't offer a way
    //       to parse them?
    // NOTE: This function is what is implemented in URKit and Hummingbird. This library instead uses a typesafe
    //       enum class to represent the registry types. This in turns prevents it from supporting custom types not
    //       defined in this library.
    // private fun parse(part: String): Pair<String, List<String>> {
    //     // Don't consider case
    //     val lowercased: String = part.lowercase()
    //
    //     // Validate URI scheme
    //     if (!lowercased.startsWith("ur:")) throw InvalidSchemeException("Invalid UR scheme '${lowercased.take(3)}'")
    //
    //     // Split the remainder into path components
    //     val fullPath: String = lowercased.drop(3)
    //     val pathComponents: List<String> = fullPath.split("/")
    //
    //     // Make sure there are at least two path components
    //     if (pathComponents.size < 2) throw InvalidPathLengthException("Invalid UR path length of ${pathComponents.size}")
    //
    //     // Make sure the first path component is a valid registry type
    //     val type: String = pathComponents.first()
    //     if (!type.isValidURType()) throw InvalidTypeException("Invalid UR type '$type'")
    //
    //     return Pair(type, pathComponents)
    // }

    // NOTE: This function is what is implemented in URKit and Hummingbird. This library instead uses a typesafe
    //       enum class to represent the registry types. This in turns prevents it from supporting custom types not
    //       defined in this library.
    /**
     * If this is the first part we see, set the type as the expected type. If it's not the first part we see, verify
     * that the type of the part is the expected type.
     *
     * @param type The type of the UR.
     */
    // private fun isCorrectType(type: String): Boolean {
    //     if (expectedType == null) {
    //         if (!type.isValidURType()) {
    //             return false
    //         } else {
    //             expectedType = type
    //             return true
    //         }
    //     } else {
    //         return type == expectedType
    //     }
    // }
}

// NOTE: These are used when validating whether strings are potentially valid UR types. This library instead elects to
//       use a RegistryType enum to represent the different types of URs, making these operations typesafe.
// public fun Char.isValidURCharacter(): Boolean {
//     return this in 'A'..'Z' || this in 'a'..'z' || this in '0'..'9' || this == '-'
// }
//
// public fun String.isValidURType(): Boolean {
//     return this.all { it.isValidURCharacter() }
// }
