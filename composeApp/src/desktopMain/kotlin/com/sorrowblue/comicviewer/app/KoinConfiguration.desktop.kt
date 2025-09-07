package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import logcat.LogPriority
import logcat.logcat
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.module

internal actual fun koinConfiguration() = KoinConfiguration {
    logger(JvmLogger)
    modules(allModules())
}

internal actual fun allModules() =
    listOf(DiModule().module, AppModule().module)

private object JvmLogger : Logger() {
    override fun display(level: Level, msg: MESSAGE) {
        val priority = when (level) {
            Level.DEBUG -> LogPriority.DEBUG
            Level.INFO -> LogPriority.INFO
            Level.WARNING -> LogPriority.WARN
            Level.ERROR -> LogPriority.ERROR
            Level.NONE -> LogPriority.VERBOSE
        }
        logcat(priority = priority) { msg }
    }
}
