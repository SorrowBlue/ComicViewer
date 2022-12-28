plugins {
    id("com.android.library")
id("org.jetbrains.kotlin.android")
id("build-logic.android.library")

}

dependencies {
    implementation(projects.data)
    implementation(projects.data.database)
    implementation(projects.data.datastore)
    implementation(projects.data.paging)
    implementation(projects.data.remote)
    implementation(projects.data.remote.client)
    implementation(projects.data.remote.client.device)
    implementation(projects.data.remote.client.smb)
    implementation(projects.data.remote.reader)
    implementation(projects.data.remote.reader.pdf)
    implementation(projects.data.remote.reader.zip)
    implementation(projects.data.service)

}
