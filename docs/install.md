# Install

## Build and deploy locally

To build the library locally and deploy to your local Maven repository, run the following command:
```shell
./gradlew publishToMavenLocal
```

Alternatively, if you have the [just](https://just.systems/) command line tool installed, you can use
```shell
just publishlocal
```

The library will be available in your local Maven repository (typically at `~/.m2/repository/` for macOS and Linux systems) under the group ID `org.kotlinbitcointools` and the artifact ID `ur`. You can import it in your project as you would any other Maven dependency provided you have your local Maven repository (`mavenLocal()`) configured as a dependency source:
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
```

```kotlin
// build.gradle.kts
implementation("org.kotlinbitcointools:ur:0.0.1-SNAPSHOT")
```

[issues]: https://github.com/kotlin-bitcoin-tools/ur/issues
