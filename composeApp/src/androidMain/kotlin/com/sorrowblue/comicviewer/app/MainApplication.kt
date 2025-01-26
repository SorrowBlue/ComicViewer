package com.sorrowblue.comicviewer.app

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.sorrowblue.comicviewer.data.di.DiModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsModule
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinConfiguration
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module

@KoinExperimentalAPI
internal class MainApplication : SplitCompatApplication(), KoinStartup, KoinComponent {

    override fun onKoinStartup() = KoinConfiguration {
        androidLogger()
        androidContext(this@MainApplication)
        modules(DiModule().module)
        modules(SettingsModule().module)
        defaultModule()
        workManagerFactory()
        logcat(LogPriority.INFO) { "onKoinStartup" }
    }

    override fun onCreate() {
        super.onCreate()
        logcat(LogPriority.INFO) { "onCreate" }
    }
}
