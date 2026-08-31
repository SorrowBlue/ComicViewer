package com.sorrowblue.comicviewer.app

import android.app.Application
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import dev.zacsweers.metro.createGraphFactory
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger

class TestApplication :
    Application(),
    AppGraphProvider<AppGraph>,
    Configuration.Provider {

    override val appGraph by lazy {
        Log.d("***********", "TestApplicationappGraph")
        createGraphFactory<AppGraph.Factory>().createAppGraph(
            InstrumentationRegistry.getInstrumentation().context,
        )
    }

    init {
        LogcatLogger.install(AndroidLogcatLogger(LogPriority.VERBOSE))
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()
}
