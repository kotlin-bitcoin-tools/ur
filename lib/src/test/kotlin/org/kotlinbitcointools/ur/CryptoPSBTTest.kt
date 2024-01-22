package org.kotlinbitcointools.ur

import org.kotlinbitcointools.ur.registry.CryptoPSBT
import org.kotlinbitcointools.ur.registry.Psbt
import org.kotlinbitcointools.ur.registry.UrBytes
import org.kotlinbitcointools.ur.utilities.RandomXoshiro256StarStar
import kotlin.test.Test
import kotlin.test.assertEquals

class CryptoPsbtTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun `Encode PSBT`() {
        val psbtHex = "70736274ff01009a020000000258e87a21b56daf0c23be8e7070456c336f7cbaa5c8757924f545887bb2abdd750000000000ffffffff838d0427d0ec650a68aa46bb0b098aea4422c071b2ca78352a077959d07cea1d0100000000ffffffff0270aaf00800000000160014d85c2b71d0060b09c9886aeb815e50991dda124d00e1f5050000000016001400aea9a2e5f0f876a588df5546e8742d1d87008f000000000000000000"
        val psbt: Psbt = Psbt(psbtHex.hexToByteArray())
        val cryptoPSBT: CryptoPSBT = CryptoPSBT(psbt)
        val urString: String = "ur:crypto-psbt/hdosjojkidjyzmadaenyaoaeaeaeaohdvsknclrejnpebncnrnmnjojofejzeojlkerdonspkpkkdkykfelokgprpyutkpaeaeaeaeaezmzmzmzmlslgaaditiwpihbkispkfgrkbdaslewdfycprtjsprsgksecdratkkhktikewdcaadaeaeaeaezmzmzmzmaojopkwtayaeaeaeaecmaebbtphhdnjstiambdassoloimwmlyhygdnlcatnbggtaevyykahaeaeaeaecmaebbaeplptoevwwtyakoonlourgofgvsjydpcaltaemyaeaeaeaeaeaeaeaeaebkgdcarh"

        // Given a PSBT, the String format of the UR is correct
        assertEquals<String>(
            expected = urString,
            actual = cryptoPSBT.toUR().toString()
        )

        // The CBOR encoding of the PSBT in the UR is correct
        val cborHex = "58A770736274FF01009A020000000258E87A21B56DAF0C23BE8E7070456C336F7CBAA5C8757924F545887BB2ABDD750000000000FFFFFFFF838D0427D0EC650A68AA46BB0B098AEA4422C071B2CA78352A077959D07CEA1D0100000000FFFFFFFF0270AAF00800000000160014D85C2B71D0060B09C9886AEB815E50991DDA124D00E1F5050000000016001400AEA9A2E5F0F876A588DF5546E8742D1D87008F000000000000000000"
        assertEquals<String>(
            expected = cborHex,
            actual = cryptoPSBT.toUR().cborBytes.toHexString(HexFormat.UpperCase)
        )
    }

    // See tests:
    //     https://github.com/BlockchainCommons/URKit/blob/f1fd22eac3d75ab1eecbb9955b6bdc8d9a65ba5a/Tests/URKitTests/URTests.swift#L12-L19
    //     https://github.com/sparrowwallet/hummingbird/blob/1a7d56a3a915b90cf8f6c2bb7d9b4fa28d021b24/src/test/java/com/sparrowwallet/hummingbird/URTest.java#L18-L24
    @Test
    fun `Single part bytes type UR`() {
        val urString: String = "ur:bytes/hdeymejtswhhylkepmykhhtsytsnoyoyaxaedsuttydmmhhpktpmsrjtgwdpfnsboxgwlbaawzuefywkdplrsrjynbvygabwjldapfcsdwkbrkch"
        val message: ByteArray = makeMessage(50, "Wolf")
        val bytesUR = UrBytes(message)

        assertEquals(
            expected = urString,
            actual = bytesUR.toUR().toString()
        )
    }
}

internal fun makeMessage(length: Int, seed: String): ByteArray {
    val rng = RandomXoshiro256StarStar(seed)
    val message: ByteArray = ByteArray(length)
    rng.nextData(message)
    return message
}
