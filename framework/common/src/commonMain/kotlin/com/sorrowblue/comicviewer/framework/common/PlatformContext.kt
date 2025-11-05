package com.sorrowblue.comicviewer.framework.common

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

expect abstract class PlatformContext

val LocalPlatformContext: ProvidableCompositionLocal<PlatformContext> =
    staticCompositionLocalOf {
        error("No PlatformContext provided")
    }

expect val PlatformContext.platformGraph: PlatformGraph

interface PlatformGraph
