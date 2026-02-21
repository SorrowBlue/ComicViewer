package com.sorrowblue.comicviewer.app

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.getPlatformGraph
import dev.zacsweers.metro.createGraphFactory
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger

class TestApplication :
    Application(),
    Configuration.Provider {
    private val appGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(
            InstrumentationRegistry.getInstrumentation().context,
            object : LicenseeHelper {
                override suspend fun loadLibraries(): ByteArray {
                    TODO("Not yet implemented")
                }
            },
        )
    }

    init {
        getPlatformGraph = { appGraph }
        LogcatLogger.install(AndroidLogcatLogger(LogPriority.VERBOSE))
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()
}
