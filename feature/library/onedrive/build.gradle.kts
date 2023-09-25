plugins {
    id("comicviewer.android.feature.dynamic-feature")
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.library.onedrive"
    resourcePrefix("onedrive")

    packaging {
        resources.excludes += "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.dtd"
        resources.excludes += "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.relaxng"
        resources.excludes += "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.w3c"

    }
}

dependencies {
    implementation(projects.framework.notification)
    implementation(projects.domain.common)
    implementation(projects.feature.library)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.microsoft.graph) {
        exclude("com.google.guava","guava")
    }
    implementation(libs.microsoft.identity.client.msal)

    implementation(libs.kotlinx.coroutines.jdk8)
}
