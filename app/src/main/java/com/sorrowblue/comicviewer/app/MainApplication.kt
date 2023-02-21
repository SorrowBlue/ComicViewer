package com.sorrowblue.comicviewer.app

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.play.core.splitcompat.SplitCompatApplication
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import logcat.AndroidLogcatLogger
import logcat.LogPriority

@HiltAndroidApp
class MainApplication : SplitCompatApplication(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            AndroidLogcatLogger.installOnDebuggableApp(this, LogPriority.VERBOSE)
        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()
}
