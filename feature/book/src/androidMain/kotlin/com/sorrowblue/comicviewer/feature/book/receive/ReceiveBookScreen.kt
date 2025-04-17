package com.sorrowblue.comicviewer.feature.book.receive

/*
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
*/
