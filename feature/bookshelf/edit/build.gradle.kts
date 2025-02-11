plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.soil.form)
                implementation("io.github.vinceglb:filekit-compose:0.8.8")
                implementation("net.thauvin.erik.urlencoder:urlencoder-lib:1.6.0")
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.bookshelf.edit"
    resourcePrefix("bookshelf_edit")
}
