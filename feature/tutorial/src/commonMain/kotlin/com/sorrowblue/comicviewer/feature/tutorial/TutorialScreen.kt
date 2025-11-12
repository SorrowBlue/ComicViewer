package com.sorrowblue.comicviewer.feature.tutorial

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.tutorial.component.TutorialBottomBar
import com.sorrowblue.comicviewer.feature.tutorial.section.ArchiveSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheetUiState
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.WelcomeSheet
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

internal enum class TutorialSheet {
    WELCOME,
    ARCHIVE,
    DOCUMENT,
    READING_DIRECTION,
}

internal data class TutorialScreenUiState(
    val list: List<TutorialSheet> = TutorialSheet.entries,
    val directionSheetUiState: DirectionSheetUiState = DirectionSheetUiState(),
)

@Composable
internal fun TutorialScreen(
    uiState: TutorialScreenUiState,
    pageState: PagerState,
    onNextClick: () -> Unit,
    onBindingDirectionChange: (BindingDirection) -> Unit,
) {
    Scaffold(
        bottomBar = { TutorialBottomBar(pageState, onNextClick) },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { contentPadding ->
        HorizontalPager(state = pageState) {
            when (uiState.list[it]) {
                TutorialSheet.WELCOME -> WelcomeSheet(contentPadding = contentPadding)
                TutorialSheet.ARCHIVE -> ArchiveSheet(contentPadding = contentPadding)
                TutorialSheet.DOCUMENT -> DocumentSheet(contentPadding = contentPadding)

                TutorialSheet.READING_DIRECTION -> DirectionSheet(
                    uiState = uiState.directionSheetUiState,
                    onBindingDirectionChange = onBindingDirectionChange,
                    contentPadding = contentPadding,
                )
            }
        }
    }
    LocalMinimumInteractiveComponentSize
}

@Preview
@Composable
private fun TutorialScreenPreview() {
    PreviewTheme {
        TutorialScreen(
            uiState = TutorialScreenUiState(),
            pageState = rememberPagerState { TutorialSheet.entries.size },
            onNextClick = {},
            onBindingDirectionChange = {},
        )
    }
}
