package com.sorrowblue.comicviewer.feature.tutorial

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Preview
@Composable
private fun TutorialScreenPreview() {
    PreviewTheme {
        TutorialScreen(
            uiState = TutorialScreenUiState(),
            pageState = rememberPagerState { 3 },
            onNextClick = {},
            onBindingDirectionChange = {}
        )
    }
}
