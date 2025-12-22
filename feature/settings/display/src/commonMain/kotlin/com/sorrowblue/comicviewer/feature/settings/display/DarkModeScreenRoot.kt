package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.runtime.Composable

@Composable
context(context: DarkModeScreenContext)
internal fun DarkModeScreenRoot(onDismissRequest: () -> Unit, onComplete: () -> Unit) {
    val state = rememberDarkModeScreenState()
    DarkModeScreen(
        uiState = state.uiState,
        onDismissRequest = onDismissRequest,
        onDarkModeChange = {
            state.onDarkModeChange(it) {
                onComplete()
            }
        },
    )
}
