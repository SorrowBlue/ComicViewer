/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.CloseableGraph
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@OptIn(ExperimentalMetroApi::class)
actual interface AppGraph :
    ViewModelGraph,
    NavigationGraph,
    CloseableGraph {
    actual val context: PlatformContext
}
