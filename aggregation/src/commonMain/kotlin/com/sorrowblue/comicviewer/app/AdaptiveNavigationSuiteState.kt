package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKey
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionKey
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryKey
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterKey
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.framework.ui.navigation.NavigationKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator

@Composable
fun rememberAdaptiveNavigationSuiteState(navigator: Navigator): AdaptiveNavigationSuiteState {
    val state = remember {
        AdaptiveNavigationSuiteStateImpl(navigator = navigator)
    }
    return state
}

private class AdaptiveNavigationSuiteStateImpl(private val navigator: Navigator) :
    AdaptiveNavigationSuiteState {
    override val navigationKeys =
        listOf(BookshelfKey.List, CollectionKey.List, ReadLaterKey.List, HistoryKey.List)

    override fun onNavigationClick(key: NavigationKey) {
        navigator.navigate(key)
    }
}
