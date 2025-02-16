package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

internal actual fun setupDi() = KoinConfiguration {
    logcat(LogPriority.INFO) { "onKoinStartup" }
    androidLogger()
    modules(DiModule().module)
    modules(SettingsModule().module)
    defaultModule()
    workManagerFactory()
}
