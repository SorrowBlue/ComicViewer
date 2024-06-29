package com.sorrowblue.comicviewer.feature.book.receive

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.annotation.parameters.DeepLink
import com.sorrowblue.comicviewer.feature.book.BookScreen
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.feature.book.navigation.BookGraphTransitions
import logcat.logcat

@Destination<RootGraph>(
    start = true,
    style = BookGraphTransitions::class,
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            mimeType = "application/pdf",
            uriPattern = "book"
        ),
        DeepLink(
            action = Intent.ACTION_VIEW,
            mimeType = "application/zip",
            uriPattern = "book"
        )
    ]
)
@Composable
internal fun ReceiveBookScreen() {
    val activity = LocalContext.current as ComponentActivity
    logcat("BookScreen2") { "data=${activity.intent?.data}" }
    val state: ReceiveBookScreenState = rememberReceiveBookScreenState()

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
