/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.bitcointools.ur

public interface RegistryItem {
    public val registryType: RegistryType
    public fun toUR(): UR
}
