package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class, additionalScopes = [DataScope::class])
internal interface TestAppGraph {
    val database: ComicViewerDatabase
    val platformContext: PlatformContext

    @DependencyGraph.Factory
    fun interface Factory {
        fun createTestAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}

internal expect fun createPlatformContext(): PlatformContext
