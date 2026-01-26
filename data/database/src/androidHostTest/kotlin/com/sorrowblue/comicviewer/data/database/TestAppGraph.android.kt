package com.sorrowblue.comicviewer.data.database

import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

internal actual fun createPlatformContext(): PlatformContext =
    InstrumentationRegistry.getInstrumentation().context

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
internal actual interface TestAppGraph {
    actual val database: ComicViewerDatabase
    actual val platformContext: PlatformContext

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createTestAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}
