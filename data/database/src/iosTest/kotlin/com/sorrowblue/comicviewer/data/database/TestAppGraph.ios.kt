package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.di.DatabaseProviders
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.IosDatabaseProviders
import com.sorrowblue.comicviewer.framework.common.IosContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

internal actual fun createPlatformContext(): PlatformContext = TestIosContext()

class TestIosContext : IosContext()

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class]
)
internal actual interface TestAppGraph : IosDatabaseProviders, DatabaseProviders {
    actual val database: ComicViewerDatabase
    actual val platformContext: PlatformContext

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createTestAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}
