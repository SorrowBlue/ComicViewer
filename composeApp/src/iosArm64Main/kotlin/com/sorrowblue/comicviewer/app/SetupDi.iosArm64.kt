package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import logcat.LogcatLogger
import logcat.PrintLogger
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module
import org.koin.ksp.generated.*

internal actual fun setupDi() = KoinConfiguration {
    LogcatLogger.install(PrintLogger)
    modules(DiModule().module)
    modules(SettingsModule().module)
    defaultModule()
    module {
        defineAndroidSplitInstallManager()
    }
}
