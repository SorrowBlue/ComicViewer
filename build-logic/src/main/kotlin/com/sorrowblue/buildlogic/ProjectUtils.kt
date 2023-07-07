package com.sorrowblue.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.internal.catalog.DelegatingProjectDependency
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

private fun Project.inferNameSpace(s: String): String {
    val a = if (parent?.name == rootProject.name) null else parent?.name
    return if (a == null) {
        "com.sorrowblue.comicviewer.$s"
    } else {
        parent!!.inferNameSpace("$a.$s")
    }
}

fun <T : Any?> NamedDomainObjectContainer<T>.prerelease(action: T.() -> Unit) {
    create("prerelease") {
        action(this)
    }
}

fun <T : Any?> NamedDomainObjectContainer<T>.internal(action: T.() -> Unit) {
    create("internal") {
        action(this)
    }
}

fun <T : Any?> NamedDomainObjectContainer<T>.release(action: T.() -> Unit) {
    create("release") {
        action(this)
    }
}

fun <T : Any?> NamedDomainObjectContainer<T>.debug(action: T.() -> Unit) {
    getByName("debug") {
        action(this)
    }
}

internal fun CommonExtension<*, *, *, *, *>.applyCommonConfigure(project: Project) {
    namespace = project.inferNameSpace(project.name).also {
        project.logger.lifecycle("nameSpace=$it")
    }
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

internal fun CommonExtension<*, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

fun DelegatingProjectDependency.projectString(): String {
    val n = ":" + dependencyProject.name
    return if (dependencyProject.parent == null) n else dependencyProject.parent!!.projectString(n)
}

fun Project.projectString(a: String): String {
    val n = ":$name$a"
    return if (parent == null || parent!!.name == "ComicViewer") n else parent!!.projectString(n)
}

internal fun Project.plugins(action: PluginManager.() -> Unit) {
    action(pluginManager)
}

internal fun Project.kotlin(configure: Action<KotlinAndroidProjectExtension>): Unit =
    extensions.configure("kotlin", configure)

internal fun Project.kapt(configure: Action<KaptExtension>) =
    extensions.configure("kapt", configure)

internal val Project.libs: VersionCatalog
    get() {
        return extensions.getByType<VersionCatalogsExtension>().named("libs")
    }

internal fun PluginManager.id(name: String) = apply(name)

internal fun VersionCatalog.version(name: String): String {
    return findVersion(name).get().requiredVersion
}
