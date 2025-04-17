package com.sorrowblue.comicviewer.framework.common.starup

import com.sorrowblue.comicviewer.framework.common.Initializer
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat
import org.koin.core.annotation.Factory

@Factory
class LogcatInitializer : Initializer<LogcatLogger.Companion> {
    override fun create(): LogcatLogger.Companion {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
        logcat(LogPriority.INFO) { "Initialized logcat." }
        return LogcatLogger.Companion
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> {
        return emptyList()
    }
}
