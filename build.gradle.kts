plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-io:commons-io:2.15.1")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.google.guava:guava:32.1.3-jre")


    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-framework-datatest:5.8.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
