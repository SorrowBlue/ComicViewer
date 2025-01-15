package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffoldUiState
import com.sorrowblue.comicviewer.app.component.MainScreenTab
import com.sorrowblue.comicviewer.framework.ui.EventFlow

internal sealed interface ComicViewerAppEvent {

    data class Navigate(
        val route: Any,
        val navOptions: NavOptions? = null,
    ) : ComicViewerAppEvent
}

@Composable
internal fun rememberComicViewerAppState(
    navController: NavHostController = rememberNavController(),
): ComicViewerAppState {
    return remember {
        ComicViewerAppStateImpl(
            navController = navController,
        )
    }
}

internal interface ComicViewerAppState {

    val navController: NavHostController
    val uiState: ComicViewerScaffoldUiState
    val events: EventFlow<ComicViewerAppEvent>

    fun onTabSelect(tab: MainScreenTab)
}

private class ComicViewerAppStateImpl(override val navController: NavHostController) :
    ComicViewerAppState {

    override var uiState: ComicViewerScaffoldUiState by mutableStateOf(ComicViewerScaffoldUiState())
        private set

    override val events: EventFlow<ComicViewerAppEvent> = EventFlow()

    init {
        uiState = uiState.copy(currentTab = MainScreenTab.Bookshelf(""))
    }

    override fun onTabSelect(tab: MainScreenTab) {
        uiState = uiState.copy(currentTab = tab)
    }
}
