package com.sorrowblue.comicviewer.framework.common

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

@Suppress("AbstractClassCanBeInterface")
expect abstract class PlatformContext

val LocalPlatformContext: ProvidableCompositionLocal<PlatformContext> =
    staticCompositionLocalOf {
        error("No PlatformContext provided")
    }

expect val PlatformContext.platformGraph: PlatformGraph

fun <T : Any> PlatformContext.require(): T {
    @Suppress("UNCHECKED_CAST")
    return this.platformGraph as T
}

interface PlatformGraph
