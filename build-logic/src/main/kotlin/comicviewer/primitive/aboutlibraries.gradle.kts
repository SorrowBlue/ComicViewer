package comicviewer.primitive

import com.mikepenz.aboutlibraries.plugin.StrictMode

plugins {
    com.mikepenz.aboutlibraries.plugin
}

aboutLibraries {
    collect {
        includePlatform.set(true)
        fetchRemoteLicense.set(true)
        val githubApiToken: String? by project
        gitHubApiToken.set(githubApiToken)
    }
    license {
        strictMode.set(StrictMode.FAIL)
        allowedLicenses.addAll(
            "Apache-2.0",
            "ASDKL",
            "LGPL-2.1",
            "LGPL-2.1-or-later",
            "MIT",
            "PCSDKToS",
            "Unlicense",
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
                    "io.github.irgaly.navigation3.resultstate",
                    "net.java.dev.jna",
                    "org.jetbrains.kotlinx",
                ),
            ),
        )
    }
}
