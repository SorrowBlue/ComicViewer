plugins {
    id("build-logic.android.library")
}

dependencies {
    implementation(projects.data.database)
    implementation(projects.data.datastore)
    implementation(projects.data.paging)
    implementation(projects.data.remote.device)
    implementation(projects.data.remote.smb)
    implementation(projects.data.service)

}
