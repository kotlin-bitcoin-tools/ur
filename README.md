# Readme
⚠️This library is not currently production-ready. Use at your own risk. ⚠️  
<br>

This library is an implementation of the [Uniform Resources] specification. It is written in Kotlin and is intended to be used in Kotlin Multiplatform projects. Please help us review it and make it production-ready! The API is still in flux, and we are open to suggestions. See the issues for discussion items and design decisions.

The main goals of this library are:
- [ ] 1. UR-specification compliant
- [ ] 2. Well tested
- [ ] 3. Well documented
- [ ] 4. Production ready
- [ ] 5. Usable in KMP projects (JVM and iOS platforms)

The library is not currently available on Maven Central. To build locally and deploy to your local Maven repository, see the [build instructions](#build-instructions).  
<br>

## Other Implementations
This library is built using inspiration from 3 other major implementations of the spec:
- [URKit]
- [Hummingbird]
- [ur-rs]  
<br>

## Kotlin Multiplatform
The library is currently only compatible with the JVM target because of the CBOR encoding being performed by the [jackson-dataformat-cbor] library, which is not KMP-compatible. This will be remediated by switching to the [official Kotlin serialization library]. Unfortunately, the library currently encodes the `ByteArray` type as [CBOR major type 4 instead of 2], and this makes the serialization incompatible with the spec. There is an [annotation] that allows encoding class fields to major type 2, but this annotation currently does not work on simple `ByteArray`s or on value classes. This issue is known and a fix is likely to arrive in the next version or so. See related [issues] and potential fix in this [PR].  
<br>

## Build Instructions
To build the library locally and deploy to your local Maven repository, run the following command:
```shell
./gradlew publishToMavenLocal
```

The library will be available in your local Maven repository (typically at `~/.m2/repository/` for macOS and Linux systems) under the group ID `org.bitcointools` and the artifact ID `ur`. You can import it in your project as you would any other Maven dependency, provided you have your local Maven repository (`mavenLocal()`) configured as a dependency source:
```kotlin
// root-level build.gradle.kts
allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

// app-level build.gradle.kts
implementation("org.bitcointools:ur:0.0.1-SNAPSHOT")
```

[Uniform Resources]: https://github.com/BlockchainCommons/Research/blob/master/papers/bcr-2020-005-ur.md
[URKit]: https://github.com/BlockchainCommons/URKit
[Hummingbird]: https://github.com/sparrowwallet/hummingbird
[ur-rs]: https://github.com/dspicher/ur-rs
[jackson-dataformat-cbor]: https://central.sonatype.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-cbor/2.15.2
[official Kotlin serialization library]: https://github.com/Kotlin/kotlinx.serialization
[CBOR major type 4 instead of 2]: https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/formats.md#byte-arrays-and-cbor-data-types
[annotation]: https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-byte-string/
[issues]: https://github.com/Kotlin/kotlinx.serialization/issues?q=is%3Aissue+is%3Aopen+2187+2037+
[PR]: https://github.com/Kotlin/kotlinx.serialization/pull/2412
