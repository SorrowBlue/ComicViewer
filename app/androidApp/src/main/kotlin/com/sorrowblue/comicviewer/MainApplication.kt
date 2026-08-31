/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import android.app.Application
import androidx.work.Configuration
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import logcat.LogPriority
import logcat.logcat

@Suppress("Registered")
internal class MainApplication :
    Application(),
    MetroApplication,
    AppGraphProvider<AppGraph>,
    Configuration.Provider {

    override val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(
            this,
        )
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
