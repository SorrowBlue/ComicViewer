package com.sorrowblue.comicviewer

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.internal.catalog.DelegatingProjectDependency
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

fun DelegatingProjectDependency.projectString(): String {
    val n = ":" + dependencyProject.name
    return if (dependencyProject.parent == null) n else dependencyProject.parent!!.projectString(n)
}

fun Project.projectString(a: String): String {
    val n = ":$name$a"
    return if (parent == null || parent!!.name == rootProject.name) n else parent!!.projectString(n)
}

fun Project.parentName(): String {
    return parent?.let { it.parentName() + ".$name" } ?: name
}

internal fun Project.kotlin(configure: Action<KotlinAndroidProjectExtension>): Unit =
    extensions.configure("kotlin", configure)

internal val Project.libs: VersionCatalog
    get() {
        return extensions.getByType<VersionCatalogsExtension>().named("libs")
    }

internal fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

internal fun DependencyHandlerScope.detektPlugins(dependencyNotation: Any) {
    add("detektPlugins", dependencyNotation)
}

internal fun DependencyHandlerScope.testImplementation(dependencyNotation: Any) {
    add("testImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: Any) {
    add("androidTestImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.ksp(dependencyNotation: Any) {
    add("ksp", dependencyNotation)
}
