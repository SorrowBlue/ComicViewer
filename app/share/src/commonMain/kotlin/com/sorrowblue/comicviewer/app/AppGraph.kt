package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
expect interface AppGraph {
    val context: PlatformContext

    @DependencyGraph.Factory
    fun interface Factory {
        fun createAppGraph(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): AppGraph
    }
}
