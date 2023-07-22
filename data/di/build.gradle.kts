plugins {
    id("build-logic.android.library")
}

dependencies {
    implementation(projects.data.coil)
    implementation(projects.data.database)
    implementation(projects.data.datastore)
    implementation(projects.data.paging)
    implementation(projects.data.reader.zip)
    implementation(projects.data.storage.device)
    implementation(projects.data.storage.smb)
    implementation(projects.data.service)

}
