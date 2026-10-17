/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.IosContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
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

@DependencyGraph(scope = AppScope::class)
internal interface TestAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createTestAppGraph(@Provides applicationContext: PlatformContext): TestAppGraph
    }
}

internal class IosApplication : IosContext(), AppGraphProvider<AppGraph> {
    override val appGraph: AppGraph by lazy {
        createGraphFactory<TestAppGraph.Factory>().createTestAppGraph(this)
    }
}
