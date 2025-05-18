package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.module

internal actual fun koinConfiguration() = KoinConfiguration {
    logcat(LogPriority.INFO) { "onKoinStartup" }
    androidLogger()
    workManagerFactory()
}

internal actual fun allModules() =
    listOf(DiModule().module, SettingsModule().module, AppModule().module)
