plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.gradle.java-library")
    id("org.gradle.maven-publish")
    id("org.jetbrains.dokka") version "1.9.0"
}

group = "org.kotlinbitcointools"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0-rc1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-cbor:2.13.0")
    implementation("com.google.guava:guava:33.0.0-jre")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.22")
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
            groupId = groupId
            artifactId = "ur"
            version = version

            from(components["java"])
        }
    }
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            moduleName.set("ur")
            moduleVersion.set("0.0.1-SNAPSHOT")
            // includes.from("Module.md")
        }
    }
}
