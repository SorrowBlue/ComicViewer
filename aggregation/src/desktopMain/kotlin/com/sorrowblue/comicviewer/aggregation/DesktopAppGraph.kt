package com.sorrowblue.comicviewer.aggregation

import com.sorrowblue.comicviewer.AppContext
import com.sorrowblue.comicviewer.data.coil.di.CoilGraph
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.designsystem.locale.LocaleGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ThemeContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
interface DesktopAppGraph :
    PlatformGraph,
    CoilGraph.Factory,
    ThemeContext,
    LocaleGraph,
    AppContext {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createDesktopAppGraph(@Provides licenseeHelper: LicenseeHelper): DesktopAppGraph
    }
}
