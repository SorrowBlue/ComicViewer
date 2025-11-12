package com.sorrowblue.comicviewer.app

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.sorrowblue.comicviewer.aggregation.AndroidAppGraph
import com.sorrowblue.comicviewer.framework.common.PlatformApplication
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import dev.zacsweers.metro.createGraphFactory
import logcat.LogPriority
import logcat.logcat

internal class MainApplication :
    SplitCompatApplication(),
    PlatformApplication {
    override fun onCreate() {
        super.onCreate()
        logcat(LogPriority.INFO) { "onCreate" }
    }

    override val platformGraph: PlatformGraph by lazy {
        createGraphFactory<AndroidAppGraph.Factory>().createAndroidAppGraph(
            this,
            LicenseeHelperImpl(),
        )
    }
}
