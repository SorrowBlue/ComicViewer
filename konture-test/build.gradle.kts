plugins {
    alias(libs.plugins.dependencyAnalysis)
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(libs.konture)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}
