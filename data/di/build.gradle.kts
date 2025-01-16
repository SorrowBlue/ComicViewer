plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.library)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.di)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.domain.service)
                implementation(projects.data.coil)
                implementation(projects.data.database)
                implementation(projects.data.datastore)
                implementation(projects.data.reader.zip)
                implementation(projects.data.storage.client)
                implementation(projects.data.storage.device)
                implementation(projects.data.storage.smb)
            }
        }

        androidMain {
            dependencies {
                // :feature:library:box :feature:library:onedrive
                implementation(libs.squareup.okhttp3)
                implementation(libs.bouncycastle.bcprovJdk18on)
                // :feature:library:dropbox :feature:library:onedrive
                implementation(libs.fasterxml.jackson.core)
                // :feature:library:googledrive :feature:library:onedrive
                implementation(libs.google.code.gson)
                implementation(libs.androidx.credentials.playServicesAuth)
                // :feature:library:googledrive
                // Type com.google.common.util.concurrent.ListenableFuture is defined multiple times:
                implementation(libs.google.guava)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.data.di"
}
