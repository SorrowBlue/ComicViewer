/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.IosContext
import dev.zacsweers.metro.createGraphFactory

actual fun setupTest() {
    // Do nothing for iOS tests
}

actual fun tearDownTest(appGraph: AppGraph) {
    // Do nothing for iOS tests
}

actual fun createAppGraph(): AppGraph {
    return IosApplication().appGraph
}

internal class IosApplication : IosContext(), AppGraphProvider<AppGraph> {
    override val appGraph: AppGraph by lazy {
        createGraphFactory<AppGraph.Factory>().createAppGraph(this)
    }
}
