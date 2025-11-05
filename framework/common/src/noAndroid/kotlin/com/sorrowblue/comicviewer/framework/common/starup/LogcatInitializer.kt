package com.sorrowblue.comicviewer.framework.common.starup

import com.sorrowblue.comicviewer.framework.common.Initializer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.binding
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat

@IntoSet
@ContributesBinding(scope = AppScope::class, binding = binding<Initializer<*>>())
@Inject
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
