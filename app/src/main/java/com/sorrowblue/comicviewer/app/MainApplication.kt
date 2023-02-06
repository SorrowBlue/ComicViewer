package com.sorrowblue.comicviewer.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import logcat.AndroidLogcatLogger
import logcat.LogPriority


@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this, LogPriority.VERBOSE)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()
}
