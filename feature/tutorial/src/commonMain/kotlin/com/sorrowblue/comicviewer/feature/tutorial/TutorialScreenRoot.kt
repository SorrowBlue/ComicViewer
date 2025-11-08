package com.sorrowblue.comicviewer.feature.tutorial

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.framework.ui.BackHandler

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

    BackHandler(state.enabledBack, state::onBack)
}
