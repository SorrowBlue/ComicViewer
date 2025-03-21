package com.sorrowblue.comicviewer

import app.cash.licensee.LicenseeExtension
import app.cash.licensee.UnusedAction
import com.mikepenz.aboutlibraries.plugin.AboutLibrariesExtension
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/** Configure base Kotlin options */
internal inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() =
    configure<T> {
        jvmToolchain {
            vendor.set(JvmVendorSpec.ADOPTIUM)
            languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
        }
        val warningsAsErrors: String? by project
        when (this) {
            is KotlinAndroidProjectExtension -> compilerOptions
            is KotlinMultiplatformExtension -> compilerOptions
            else -> TODO("Unsupported project extension $this ${T::class}")
        }.allWarningsAsErrors.set(warningsAsErrors.toBoolean())
    }

internal fun Project.configureLicensee() {
    configure<LicenseeExtension> {
        allow("Apache-2.0")
        allow("EPL-1.0")
        allow("BSD-2-Clause")
        allow("MIT")
        allow("Unlicense")
        allowUrl("https://developer.android.com/studio/terms.html")
        allowUrl("https://developer.android.com/guide/playcore/license")
        allowUrl("https://opensource.org/license/LGPL-2.1")
        allowUrl("http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt")
        allowUrl("https://www.gnu.org/licenses/agpl-3.0.html")

        allowUrl("https://www.gnu.org/licenses/lgpl.txt")
        allowUrl("https://www.bouncycastle.org/licence.html")
        allowUrl("http://www.7-zip.org/license.txt")
        allowUrl("https://github.com/hypfvieh/dbus-java/blob/master/LICENSE")

        // MIT
        allowUrl("https://github.com/vinceglb/FileKit/blob/main/LICENSE")
        allowUrl("https://opensource.org/license/mit")
        allowUrl("https://github.com/zacharee/KMPFile/blob/main/LICENSE")

        unusedAction(UnusedAction.IGNORE)
    }
}

internal fun Project.configureAboutLibraries() {
    configure<AboutLibrariesExtension> {
        registerAndroidTasks = false
    }
}
