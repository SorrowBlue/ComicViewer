package com.sorrowblue.comicviewer.app

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionKey
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryKey
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterKey
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey

@Composable
fun rememberAdaptiveNavigationSuiteState(
    appNavigationState: Navigation3State,
): AdaptiveNavigationSuiteState {
    val navigationSuiteType = NavigationSuiteScaffoldDefaults.navigationSuiteType(
        currentWindowAdaptiveInfo(),
    )
    val state = remember {
        AdaptiveNavigationSuiteStateImpl(
            appNavigationState = appNavigationState,
            navigationSuiteType = navigationSuiteType,
        )
    }
    state.navigationSuiteType = navigationSuiteType
    LaunchedEffect(appNavigationState.currentBackStack.lastOrNull()) {
        when (val screenKey = appNavigationState.currentBackStack.lastOrNull()) {
            is BookshelfKey,
            is CollectionKey,
            is ReadLaterKey,
            is HistoryKey,
            -> {
                state.currentNavItem = screenKey
            }
        }
    }
    return state
}

private class AdaptiveNavigationSuiteStateImpl(
    private val appNavigationState: Navigation3State,
    override var navigationSuiteType: NavigationSuiteType,
) : AdaptiveNavigationSuiteState {
    override val navItems: List<NavigationKey> =
        listOf(BookshelfKey.List, CollectionKey.List, ReadLaterKey.List, HistoryKey.List)

    override var currentNavItem: NavigationKey by mutableStateOf(BookshelfKey.List)

    override fun onNavItemClick(navItem: NavigationKey) {
        currentNavItem = navItem
        when (navItem) {
            is BookshelfKey -> {
                appNavigationState.currentBackStack.add(BookshelfKey.List)
            }

            is CollectionKey -> {
                appNavigationState.currentBackStack.add(CollectionKey.List)
            }

            is HistoryKey -> {
                appNavigationState.currentBackStack.add(HistoryKey.List)
            }

            is ReadLaterKey -> {
                appNavigationState.currentBackStack.add(ReadLaterKey.List)
            }
        }
    }
}
