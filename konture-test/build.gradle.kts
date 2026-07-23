plugins {
    kotlin("jvm")
    id("io.github.baole.konture")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation(libs.konture)
    testImplementation(libs.kotlin.test)
}

rootProject.subprojects.filter { it != project }.forEach {
    evaluationDependsOn(it.path)
}

tasks.test {
    useJUnitPlatform()
}

