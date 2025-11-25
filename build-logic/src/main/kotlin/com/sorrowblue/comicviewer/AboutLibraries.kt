package com.sorrowblue.comicviewer

import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate

fun Project.configureAboutLibraries() {
    configure<AboutLibrariesExtension> {
        collect {
            includePlatform.set(true)
            fetchRemoteLicense.set(true)
            val githubApiToken: String? by project
            gitHubApiToken.set(githubApiToken)
        }

        license {
            strictMode.set(com.mikepenz.aboutlibraries.plugin.StrictMode.FAIL)
            allowedLicenses.addAll(
                "Apache-2.0",
                "MIT",
                "ASDKL",
                "PCSDKToS",
                "Unlicense",
                "LGPL-2.1",
                "LGPL-2.1-or-later",
            )
            allowedLicensesMap.putAll(
                mapOf(
                    "Bouncy Castle Licence" to listOf("org.bouncycastle"),
                    "BSD-3-Clause" to listOf("org.hamcrest"),
                    "EPL-1.0" to listOf("junit"),
                    "GNU Lesser General Public License version 2.1" to listOf(
                        "com.sorrowblue.sevenzipjbinding",
                    ),
                    "GNU Lesser General Public License, version 2.1" to listOf("org.codelibs"),
                    "unRAR restriction" to listOf("net.sf.sevenzipjbinding"),
                    "LGPL" to listOf("net.sf.sevenzipjbinding"),
                    "Other" to listOf(
                        "org.jetbrains.kotlinx",
                        "net.java.dev.jna",
                        "io.github.irgaly.navigation3.resultstate"
                    ),
                ),
            )
        }
    }
}
