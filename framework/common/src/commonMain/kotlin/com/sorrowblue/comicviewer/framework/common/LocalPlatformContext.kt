package com.sorrowblue.comicviewer.framework.common

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPlatformContext: ProvidableCompositionLocal<PlatformContext> =
    staticCompositionLocalOf {
        error("No PlatformContext provided")
    }

fun providePlatformContext(context: PlatformContext) = LocalPlatformContext provides context
