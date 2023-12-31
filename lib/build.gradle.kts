plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("java-library")
    id("maven-publish")
    // id("java")
}

val libraryVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.13.0")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.0")
        }
    }
}

kotlin {
    explicitApi()
}

java {
    // sourceCompatibility = JavaVersion.VERSION_17
    // targetCompatibility = JavaVersion.VERSION_17

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

sourceSets {
    main {
        java.srcDirs("src/main/kotlin")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.bitcointools"
            artifactId = "ur"
            version = libraryVersion

            from(components["java"])
        }
    }
}
