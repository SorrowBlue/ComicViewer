/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.common.starup

import com.sorrowblue.comicviewer.framework.common.Initializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat

@ContributesIntoSet(AppScope::class)
class LogcatInitializer : Initializer<LogcatLogger.Companion> {
    override fun create(): LogcatLogger.Companion {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
        logcat(LogPriority.INFO) { "Initialized logcat." }
        return LogcatLogger.Companion
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> = emptyList()
}
