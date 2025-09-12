package com.sorrowblue.comicviewer.app

import com.google.android.play.core.splitcompat.SplitCompatApplication
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration
import org.koin.ksp.generated.koinConfiguration

@KoinExperimentalAPI
internal class MainApplication : SplitCompatApplication(), KoinStartup {

    override fun onKoinStartup() = koinConfiguration {
        logcat(LogPriority.INFO) { "onKoinStartup" }
        MainApp.koinConfiguration {
            androidLogger()
            androidContext(this@MainApplication)
            workManagerFactory()
        }.invoke(this)
    }

    override fun onCreate() {
        super.onCreate()
        logcat(LogPriority.INFO) { "onCreate" }
    }
}
