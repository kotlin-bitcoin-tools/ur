/*
 * Copyright 2023 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE.txt file.
 */

package org.kotlinbitcointools.ur.registry

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import org.kotlinbitcointools.ur.UR

public class UrBytes(
    public val data: ByteArray,
): RegistryItem {
    override val registryType: RegistryType = RegistryType.BYTES

    public override fun toUR(): UR {
        val objectMapper = ObjectMapper(CBORFactory())
        val cbor: ByteArray = objectMapper.writeValueAsBytes(data)
        return UR(registryType, cbor)
    }
}
