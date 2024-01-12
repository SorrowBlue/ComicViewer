plugins {
    id("comicviewer.android.feature.dynamic-feature")
    id("comicviewer.android.koin")
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
    implementation(projects.domain.model)
    implementation(projects.feature.library)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.microsoft.graph) {
        exclude("com.google.guava", "guava")
    }
    implementation(libs.microsoft.identity.client.msal) {
        exclude(group = "io.opentelemetry")
    }
    implementation("io.opentelemetry:opentelemetry-api:1.34.1")

    implementation(libs.kotlinx.coroutines.jdk8)
}
