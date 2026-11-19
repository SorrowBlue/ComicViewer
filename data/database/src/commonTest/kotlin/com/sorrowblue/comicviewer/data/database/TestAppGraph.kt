package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.DatabaseTest.FakeBindings
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.CloseableGraph
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createDynamicGraphFactory

@OptIn(ExperimentalMetroApi::class)
@DependencyGraph(AppScope::class)
internal expect interface TestAppGraph : CloseableGraph {
    val database: ComicViewerDatabase

    @DependencyGraph.Factory
    fun interface Factory {
        fun createTestAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}

internal fun createFactory() = createDynamicGraphFactory<TestAppGraph.Factory>(FakeBindings)

internal expect fun createPlatformContext(): PlatformContext
