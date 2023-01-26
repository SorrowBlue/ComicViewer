package com.sorrowblue.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.PluginManager

abstract class AndroidCommonPlugin(private val projectBlock: Project.() -> Unit) : Plugin<Project> {
    override fun apply(target: Project) {
        projectBlock.invoke(target)
    }

}

fun Project.plugins(action: PluginManager.() -> Unit) {
    action(pluginManager)
}

fun PluginManager.id(name: String) = apply(name)
