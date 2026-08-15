/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavigationEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@Composable
context(context: PlatformContext)
fun MetroContent(content: @Composable () -> Unit) {
    val mvmf = context.appGraph<ViewModelGraph>().metroViewModelFactory
    CompositionLocalProvider(LocalMetroViewModelFactory provides mvmf) {
        content()
    }
}

@DependencyGraph(scope = AppScope::class)
expect interface AppGraph :
    ViewModelGraph,
    NavigationGraph {
    val context: PlatformContext

    @DependencyGraph.Factory
    fun interface Factory {
        fun createAppGraph(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): AppGraph
    }
}

interface NavigationGraph {
    val navigationEntryProvider: Set<NavigationEntryProvider>
    val navKeySubclassMap: Set<NavKeyEntry>
    val navigationKeys: Set<NavigationKey>
}
