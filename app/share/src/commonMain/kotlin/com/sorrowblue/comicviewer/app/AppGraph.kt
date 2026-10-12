/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.appGraph
import com.sorrowblue.comicviewer.framework.navigation.AppContentDecoratorProviders
import com.sorrowblue.comicviewer.framework.navigation.NavEntryDecoratorProvider
import com.sorrowblue.comicviewer.framework.navigation.NavKeyEntry
import com.sorrowblue.comicviewer.framework.navigation.NavigationEntryProvider
import com.sorrowblue.comicviewer.framework.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.FloatingActionContent
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

/**
 * [Navigator] にバインドされた [NavEntry] プロバイダを生成・記憶します。
 *
 * @param navigator ナビゲーションを管理する [Navigator]
 * @return [NavKey] に対応する [NavEntry] を返す関数
 */
@Composable
context(context: PlatformContext)
internal fun rememberEntryProvider(navigator: Navigator): (NavKey) -> NavEntry<NavKey> =
    remember(navigator) {
        val graph = context.appGraph<NavigationGraph>()
        entryProvider {
            graph.navigationEntryProviders.forEach { provider ->
                provider(navigator = navigator)
            }
        }
    }

/**
 * ナビゲーションエントリのデコレータ一覧を取得する関数を生成・記憶します。
 *
 * @return 各画面の [NavEntryDecorator] 一覧を返す Composable 関数
 */
@Composable
context(context: PlatformContext)
internal fun rememberNavEntryDecorators(): @Composable () -> List<NavEntryDecorator<NavKey>> {
    val providers = remember {
        context.appGraph<NavigationGraph>().navEntryDecoratorProviders
    }
    return {
        providers.map { it.rememberNavEntryDecorator() }
    }
}

@DependencyGraph(scope = AppScope::class)
expect interface AppGraph :
    ViewModelGraph,
    NavigationGraph {
    val context: PlatformContext

    @DependencyGraph.Factory
    fun interface Factory {
        fun createAppGraph(@Provides applicationContext: PlatformContext): AppGraph
    }
}

interface NavigationGraph {
    val appContentDecoratorProviders: AppContentDecoratorProviders
    val navigationEntryProviders: Set<NavigationEntryProvider>
    val navEntryDecoratorProviders: Set<NavEntryDecoratorProvider>
    val navKeySubclassMap: Set<NavKeyEntry>
    val navigationKeys: Set<NavigationKey>
    val floatingActionContents: Set<FloatingActionContent>
}
