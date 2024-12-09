package com.sorrowblue.comicviewer

import com.android.build.api.dsl.CommonExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.the
import org.gradle.plugin.use.PluginDependency
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension

internal val Project.libs: LibrariesForLibs
    get() = the<LibrariesForLibs>()

internal fun Project.parentName(): String = parent?.let { it.parentName() + ".$name" } ?: name

internal fun Project.plugins(block: PluginManager.() -> Unit) = with(pluginManager, block)

internal fun PluginManager.id(provider: Provider<PluginDependency>) = apply(provider.get().pluginId)

internal inline fun <reified T : CommonExtension<*, *, *, *, *, *>> Project.android(crossinline block: T.() -> Unit) =
    configure<T> { block(this) }

internal inline fun <reified T : KotlinBaseExtension> Project.kotlin(crossinline block: T.() -> Unit) =
    configure<T> { block(this) }

internal fun Project.ksp(block: KspExtension.() -> Unit) = configure<KspExtension>(block)

internal fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) =
    configure<ComposeCompilerGradlePluginExtension>(block)

internal fun DependencyHandlerScope.implementation(dependencyNotation: Any) {
    add("implementation", dependencyNotation)
}

internal fun DependencyHandlerScope.detektPlugins(dependencyNotation: Any) {
    add("detektPlugins", dependencyNotation)
}

internal fun DependencyHandlerScope.testImplementation(dependencyNotation: Any) {
    add("testImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any) {
    add("debugImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.testDebugImplementation(dependencyNotation: Any) {
    add("testDebugImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.testPrereleaseImplementation(dependencyNotation: Any) {
    add("testPrereleaseImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.testInternalImplementation(dependencyNotation: Any) {
    add("testInternalImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.testReleaseImplementation(dependencyNotation: Any) {
    add("testReleaseImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: Any) {
    add("androidTestImplementation", dependencyNotation)
}

internal fun DependencyHandlerScope.ksp(dependencyNotation: Any) {
    add("ksp", dependencyNotation)
}

internal fun DependencyHandlerScope.kspDebug(dependencyNotation: Any) {
    add("kspDebug", dependencyNotation)
}
