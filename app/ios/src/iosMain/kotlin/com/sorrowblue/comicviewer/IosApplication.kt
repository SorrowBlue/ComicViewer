/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import com.sorrowblue.comicviewer.app.AppGraph
import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.IosContext
import dev.zacsweers.metro.createGraphFactory

internal class IosApplication : IosContext(), AppGraphProvider<AppGraph> {
    override val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(this, LicenseeHelperImpl())
    }
}
