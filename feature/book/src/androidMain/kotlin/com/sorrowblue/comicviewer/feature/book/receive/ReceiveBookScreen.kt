package com.sorrowblue.comicviewer.feature.book.receive

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.annotation.DeepLink
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.book.BookScreen
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraph
import kotlinx.serialization.Serializable

@Serializable
internal data object ReceiveBook

@Destination<ReceiveBook>(
    graph = BookNavGraph::class,
    deeplinks = [
        DeepLink(action = Intent.ACTION_VIEW, mimeType = "application/pdf"),
        DeepLink(action = Intent.ACTION_VIEW, mimeType = "application/zip")
    ]
)
@Composable
internal fun ReceiveBookScreen(navBackStackEntry: NavBackStackEntry) {
    val intent = navBackStackEntry.savedStateHandle.get<Intent>(NavController.KEY_DEEP_LINK_INTENT)
    val state: ReceiveBookScreenState =
        rememberReceiveBookScreenState(uri = intent?.data?.toString())

    if (state.uiState is BookScreenUiState.Loaded) {
        val uiState = state.uiState as BookScreenUiState.Loaded
        BookScreen(
            uiState = uiState,
            pagerState = state.pagerState,
            currentList = state.currentList,
            onBackClick = { },
            onNextBookClick = { },
            onContainerClick = state::toggleTooltip,
            onContainerLongClick = {},
            onPageChange = state::onPageChange,
            onSettingsClick = {},
            onPageLoad = state::onPageLoaded,
        )
    }
}
