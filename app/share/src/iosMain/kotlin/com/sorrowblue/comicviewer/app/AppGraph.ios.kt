/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.startup.Initializer
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

actual interface AppGraph :
    ViewModelGraph,
    NavigationGraph {
    actual val context: PlatformContext
    val initializer: Set<Initializer<*>>
}
