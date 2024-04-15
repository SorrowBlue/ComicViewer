plugins {
    id("comicviewer.android.library")
}

android {
    namespace = "com.sorrowblue.comicviewer.di"
}

dependencies {
    implementation(projects.data.infrastructure)
    implementation(projects.data.coil)
    implementation(projects.data.database)
    implementation(projects.data.datastore)
    implementation(projects.data.reader)
    implementation(projects.data.reader.zip)
    implementation(projects.data.service)
    implementation(projects.data.storage.device)
    implementation(projects.data.storage.smb)

    // :feature:library:dropbox :feature:library:onedrive
    implementation(libs.fasterxml.jackson.core)
    // :feature:library:googledrive :feature:library:onedrive
    implementation(libs.google.code.gson)
    implementation(libs.androidx.credentials.playServicesAuth)

    // :feature:library:googledrive
    // Type com.google.common.util.concurrent.ListenableFuture is defined multiple times:
    implementation(libs.google.guava)
}
