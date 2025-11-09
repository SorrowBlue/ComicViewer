package com.sorrowblue.comicviewer.aggregation

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.designsystem.theme.ThemeContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
interface DesktopAppGraph : PlatformGraph, ThemeContext {

    @DependencyGraph.Factory
    fun interface Factory {
        fun createDesktopAppGraph(
            @Provides context: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): DesktopAppGraph
    }
}
