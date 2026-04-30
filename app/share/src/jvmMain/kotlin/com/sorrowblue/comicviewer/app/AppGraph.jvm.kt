package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(scope = AppScope::class)
actual interface AppGraph : PlatformGraph {
    actual val context: PlatformContext

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createAppGraph(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): AppGraph
    }
}
