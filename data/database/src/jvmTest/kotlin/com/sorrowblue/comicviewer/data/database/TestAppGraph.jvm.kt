package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.framework.common.AppGraphProvider
import com.sorrowblue.comicviewer.framework.common.DesktopContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

internal actual fun createPlatformContext(): PlatformContext =
    object : DesktopContext(), AppGraphProvider<TestAppGraph> {
        override val appGraph: TestAppGraph by lazy {
            createFactory().createTestAppGraph(this)
        }
    }

@DependencyGraph(AppScope::class)
internal actual interface TestAppGraph {
    actual val database: ComicViewerDatabase

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createTestAppGraph(@Provides context: PlatformContext): TestAppGraph
    }
}
