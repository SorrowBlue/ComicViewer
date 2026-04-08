package com.sorrowblue.comicviewer.framework.common.starup

import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesIntoSet
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat

@ContributesIntoSet(DataScope::class)
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
