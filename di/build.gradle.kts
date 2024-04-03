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

    // :feature:library:googledrive
    // Type com.google.common.util.concurrent.ListenableFuture is defined multiple times:
    implementation(libs.google.guava)


//    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
//    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.2")
//    com.google.android.gms:play-services-auth:20.7.0
//    └ com.google.android.gms:play-services-auth-base:18.0.4
//    └ com.google.android.gms:play-services-auth-api-phone:18.0.1
//    implementation("com.google.android.gms:play-services-auth-base:18.0.10")
//    implementation("com.google.android.gms:play-services-base:18.3.0")
//    implementation("com.google.android.gms:play-services-fido:20.1.0")
//    [:feature:library:googledrive, :feature:library:onedrive] all package the same library [com.google.android.gms:play-services-auth-api-phone;Capability: group='com.google.android.gms', name='play-services-auth-api-phone'].
//    [:feature:library:googledrive, :feature:library:onedrive] all package the same library [com.google.android.gms:play-services-auth-base;Capability: group='com.google.android.gms', name='play-services-auth-base'].
//    [:feature:library:googledrive, :feature:library:onedrive] all package the same library [com.google.android.gms:play-services-auth;Capability: group='com.google.android.gms', name='play-services-auth'].
//    [:feature:library:googledrive, :feature:library:onedrive] all package the same library [com.google.android.gms:play-services-base;Capability: group='com.google.android.gms', name='play-services-base'].
//    [:feature:library:googledrive, :feature:library:onedrive] all package the same library [com.google.android.gms:play-services-fido;Capability: group='com.google.android.gms', name='play-services-fido'].
}
