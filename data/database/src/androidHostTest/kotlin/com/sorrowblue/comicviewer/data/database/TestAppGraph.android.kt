package com.sorrowblue.comicviewer.data.database

import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.CloseableGraph
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.ExperimentalMetroApi
import dev.zacsweers.metro.Provides

internal actual fun createPlatformContext(): PlatformContext =
    InstrumentationRegistry.getInstrumentation().context

@OptIn(ExperimentalMetroApi::class)
@DependencyGraph(AppScope::class)
internal actual interface TestAppGraph : CloseableGraph {
    actual val database: ComicViewerDatabase

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createTestAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}
