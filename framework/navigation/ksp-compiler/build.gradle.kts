plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation("com.google.devtools.ksp:symbol-processing-api:2.1.10-1.0.29")
                implementation("com.squareup:kotlinpoet-ksp:2.0.0")
            }
        }
    }
}
