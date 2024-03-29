/*
 * Copyright 2023-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur

// This library defines two families of exceptions: URException and FountainException.

public abstract class URException(message: String?) : Exception(message)
public abstract class FountainException(message: String?) : Exception(message)

public class InvalidSchemeException(message: String?) : URException(message)

public class InvalidPathLengthException(message: String?) : URException(message)

public class UnsupportedRegistryTypeException(message: String?) : URException(message)

public class InvalidSequenceComponentException(message: String?) : URException(message)

public class InvalidChecksumException(message: String?) : URException(message)

public class InvalidBytewordException(message: String?) : URException(message)
