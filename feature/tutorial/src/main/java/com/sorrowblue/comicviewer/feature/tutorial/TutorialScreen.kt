package com.sorrowblue.comicviewer.feature.tutorial

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.tutorial.component.TutorialBottomBar
import com.sorrowblue.comicviewer.feature.tutorial.section.ArchiveSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheetUiState
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import com.sorrowblue.comicviewer.feature.tutorial.section.WelcomeSheet
import com.sorrowblue.comicviewer.framework.ui.LifecycleEffect
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal enum class TutorialSheet {
    WELCOME,
    ARCHIVE,
    DOCUMENT,
    READING_DIRECTION,
}

internal data class TutorialScreenUiState(
    val list: PersistentList<TutorialSheet> = TutorialSheet.entries.toPersistentList(),
    val documentSheetUiState: DocumentSheetUiState = DocumentSheetUiState.NONE,
    val directionSheetUiState: DirectionSheetUiState = DirectionSheetUiState(),
)

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
internal fun TutorialScreen(
    onComplete: () -> Unit,
    state: TutorialScreenState = rememberTutorialScreenState(),
) {
    val uiState = state.uiState
    TutorialScreen(
        uiState = uiState,
        pageState = state.pageState,
        onNextClick = { state.onNextClick(onComplete) },
        onDocumentDownloadClick = state::onDocumentDownloadClick,
        onBindingDirectionChange = state::updateReadingDirection
    )

    BackHandler(state.enabledBack, state::onBack)

    LifecycleEffect(targetEvent = Lifecycle.Event.ON_START, action = state::onStart)
    LifecycleEffect(targetEvent = Lifecycle.Event.ON_STOP, action = state::onStop)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TutorialScreen(
    uiState: TutorialScreenUiState,
    pageState: PagerState,
    onNextClick: () -> Unit,
    onDocumentDownloadClick: () -> Unit,
    onBindingDirectionChange: (BindingDirection) -> Unit,
) {
    Scaffold(
        bottomBar = { TutorialBottomBar(pageState, onNextClick) }
    ) { contentPadding ->
        HorizontalPager(state = pageState) {
            when (uiState.list[it]) {
                TutorialSheet.WELCOME -> WelcomeSheet(contentPadding = contentPadding)
                TutorialSheet.ARCHIVE -> ArchiveSheet(contentPadding = contentPadding)
                TutorialSheet.DOCUMENT -> DocumentSheet(
                    uiState = uiState.documentSheetUiState,
                    onDownloadClick = onDocumentDownloadClick,
                    contentPadding = contentPadding
                )

                TutorialSheet.READING_DIRECTION -> DirectionSheet(
                    uiState = uiState.directionSheetUiState,
                    onBindingDirectionChange = onBindingDirectionChange,
                    contentPadding = contentPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun PreviewTutorialScreen() {
    PreviewTheme {
        TutorialScreen(
            uiState = TutorialScreenUiState(),
            pageState = rememberPagerState { 3 },
            onNextClick = {},
            onDocumentDownloadClick = {},
            onBindingDirectionChange = {}
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
internal val PagerState.isLastPage: Boolean
    get() = currentPage == pageCount - 1
