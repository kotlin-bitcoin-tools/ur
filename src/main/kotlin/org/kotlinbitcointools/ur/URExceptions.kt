/*
 * Copyright 2023-2024 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur

public abstract class URException(message: String?) : Exception(message)

public class InvalidSchemeException(message: String?) : URException(message)

public class InvalidPathLengthException(message: String?) : URException(message)

public class UnsupportedRegistryTypeException(message: String?) : URException(message)
