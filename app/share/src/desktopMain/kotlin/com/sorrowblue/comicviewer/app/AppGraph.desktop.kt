package com.sorrowblue.comicviewer.app

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
actual interface AppGraph : PlatformGraph {
    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createAppGraph(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): AppGraph
    }

    actual val entries: Set<EntryProviderScope<NavKey>.(Navigator) -> Unit>
}
