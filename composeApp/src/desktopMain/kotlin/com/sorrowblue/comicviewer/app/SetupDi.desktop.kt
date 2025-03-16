package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import logcat.LogPriority
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.defineComSorrowblueComicviewerFeatureTutorialAndroidSplitInstallManager
import org.koin.ksp.generated.module

internal actual fun setupDi() = KoinConfiguration {
    LogcatLogger.install(PrintLogger)
    logger(JvmLogger)
    modules(DiModule().module, SettingsModule().module, AppModule().module)
    module {
        defineComSorrowblueComicviewerFeatureTutorialAndroidSplitInstallManager()
    }
}

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
