/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

actual interface AppGraph :
    ViewModelGraph,
    NavigationGraph,
    MetroAppComponentProviders {
    actual val context: PlatformContext

    val workerFactory: WorkerFactory

    @Provides
    fun Context.providesWorkManager(): WorkManager = WorkManager.getInstance(this)
}
