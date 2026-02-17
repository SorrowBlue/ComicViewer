import com.codingfeline.buildkonfig.compiler.FieldSpec

plugins {
    alias(libs.plugins.comicviewer.multiplatformLibrary)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidLibrary {
        namespace = "com.sorrowblue.comicviewer.data.smb"
        withHostTest {}
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.data.storage.client)
                implementation(libs.kotlinx.coroutinesCore)
                implementation(libs.filekit.compose)
                implementation(libs.okio)
                implementation(libs.androidx.coreUri)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.framework.test)
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.jcifs)
                implementation(libs.slf4j.android)
                implementation(libs.androidx.documentfile)
            }
        }
        getByName("androidHostTest") {
            dependencies {
                implementation(libs.robolectric)
            }
        }
        desktopMain {
            dependencies {
                implementation(libs.jcifs)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.kmpfile.filekit)
                implementation(libs.kmpfile.okio)
            }
        }
    }
}

buildkonfig {
    packageName = "com.sorrowblue.comicviewer.data.smb"
    objectName = "BuildTestConfig"
    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbHost",
            project.findProperty("smbHost")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.INT,
            "smbPort",
            project.findProperty("smbPort")?.toString()?.toIntOrNull()?.toString() ?: "445"
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbUsername",
            project.findProperty("smbUsername")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbDomain",
            project.findProperty("smbDomain")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbPassword",
            project.findProperty("smbPassword")?.toString().orEmpty()
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "smbPath",
            project.findProperty("smbPath")?.toString().orEmpty()
        )
    }
}
