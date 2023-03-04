@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
    id("build-logic.android.dynamic-feature")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.androidx.navigation.safeargs.kotlin)
}

android {
    resourcePrefix("onedrive")

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    packagingOptions {
        resources.excludes += "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.dtd"
        resources.excludes += "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.relaxng"
        resources.excludes += "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.w3c"

    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.framework.ui)
    implementation(projects.framework.notification)
    implementation(projects.domain)
    implementation(projects.library)

    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.microsoft.graph)
    implementation(libs.microsoft.identity.client.msal)

    implementation(libs.kotlinx.coroutines.jdk8)
}

kapt {
    correctErrorTypes = true
}
