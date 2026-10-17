/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import android.app.Application
import androidx.work.Configuration
import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import logcat.LogPriority
import logcat.logcat

@DependencyGraph(scope = AppScope::class)
internal interface AndroidAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createAndroidAppGraph(@Provides applicationContext: PlatformContext): AndroidAppGraph
    }
}

@Suppress("Registered")
internal class MainApplication :
    Application(),
    MetroApplication,
    AppGraphProvider<AppGraph>,
    Configuration.Provider {

    override val appGraph: AppGraph by lazy {
        createGraphFactory<AndroidAppGraph.Factory>().createAndroidAppGraph(
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
