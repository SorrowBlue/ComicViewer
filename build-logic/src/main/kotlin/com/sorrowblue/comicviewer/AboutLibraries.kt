package com.sorrowblue.comicviewer

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate

internal fun Project.configureAboutLibraries() {
    configure<AboutLibrariesExtension> {
        android {
            registerAndroidTasks.set(false)
        }
        collect {
            includePlatform.set(true)
            fetchRemoteLicense.set(true)
        }
        collect {
            val githubApiToken: String? by project
            gitHubApiToken.set(githubApiToken)
        }
    }
}
