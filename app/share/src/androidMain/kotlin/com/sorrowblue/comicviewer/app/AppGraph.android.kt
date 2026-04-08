package com.sorrowblue.comicviewer.app

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
actual interface AppGraph :
    PlatformGraph,
    MetroAppComponentProviders {
    actual val context: PlatformContext

    val workerFactory: WorkerFactory

    @Provides
    fun providesWorkManager(application: Context): WorkManager =
        WorkManager.getInstance(application)

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createAppGraph(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): AppGraph
    }
}
