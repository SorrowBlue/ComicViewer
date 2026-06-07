package com.sorrowblue.comicviewer.app

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(scope = AppScope::class)
actual interface AppGraph :
    ViewModelGraph,
    PlatformGraph,
    MetroAppComponentProviders {
    actual val context: PlatformContext
    actual val metroVmf: MetroViewModelFactory

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
