package com.sorrowblue.comicviewer.app

import android.app.Application
import com.sorrowblue.comicviewer.framework.common.PlatformApplication
import dev.zacsweers.metro.createGraphFactory
import logcat.LogPriority
import logcat.logcat

internal class MainApplication :
    Application(),
    PlatformApplication {
    override fun onCreate() {
        super.onCreate()
        logcat(LogPriority.INFO) { "onCreate" }
    }

    override val platformGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(
            this,
            LicenseeHelperImpl(this),
        )
    }
}
