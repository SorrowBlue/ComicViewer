package com.sorrowblue.comicviewer.app

import com.google.android.play.core.splitcompat.SplitCompatApplication
import logcat.LogPriority
import logcat.logcat
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@KoinExperimentalAPI
internal class MainApplication : SplitCompatApplication(), KoinStartup {

    override fun onKoinStartup() = KoinConfiguration {
        androidContext(this@MainApplication)
        setupDi()
    }

    override fun onCreate() {
        super.onCreate()
        logcat(LogPriority.INFO) { "onCreate" }
    }
}
