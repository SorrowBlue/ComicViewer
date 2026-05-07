package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.di.DatabaseProviders
import com.sorrowblue.comicviewer.framework.common.IosContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

internal actual fun createPlatformContext(): PlatformContext = IosContext.Companion()

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [AppScope::class],
)
internal actual interface TestAppGraph : DatabaseProviders {
    actual val database: ComicViewerDatabase

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createTestAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}
