package com.sorrowblue.comicviewer.feature.tutorial

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
context(context: TutorialScreenContext)
fun TutorialScreenRoot(onComplete: () -> Unit) {
    val state = rememberTutorialScreenState()
    TutorialScreen(
        uiState = state.uiState,
        pageState = state.pageState,
        onNextClick = { state.onNextClick(onComplete) },
        onBindingDirectionChange = state::updateReadingDirection,
    )
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = state.enabledBack,
        onBackCompleted = state::onBack,
    )
}
