package com.sorrowblue.comicviewer.app

import android.app.Application
import androidx.work.Configuration
import com.sorrowblue.comicviewer.framework.common.getPlatformGraph
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import logcat.LogPriority
import logcat.logcat

class MainApplication :
    Application(),
    MetroApplication,
    Configuration.Provider {
    private val appGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(
            this,
            LicenseeHelperImpl(this),
        )
    }

    init {
        getPlatformGraph = { appGraph }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(appGraph.workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        logcat(LogPriority.INFO) { "onCreate" }
    }

    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph
}
