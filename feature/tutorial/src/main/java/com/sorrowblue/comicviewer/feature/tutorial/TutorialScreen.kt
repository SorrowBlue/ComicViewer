package com.sorrowblue.comicviewer.feature.tutorial

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.tutorial.component.TutorialBottomBar
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialGraphTransitions
import com.sorrowblue.comicviewer.feature.tutorial.section.ArchiveSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheetUiState
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import com.sorrowblue.comicviewer.feature.tutorial.section.WelcomeSheet
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
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

fun interface TutorialScreenNavigator {
    fun onComplete()
}

@Destination<TutorialGraph>(
    start = true,
    style = TutorialGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
fun TutorialScreen(navigator: TutorialScreenNavigator) {
    TutorialScreen(onComplete = navigator::onComplete)
}

@Composable
private fun TutorialScreen(
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

    LifecycleEventEffect(event = Lifecycle.Event.ON_START, onEvent = state::onStart)
    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP, onEvent = state::onStop)
}

@Composable
private fun TutorialScreen(
    uiState: TutorialScreenUiState,
    pageState: PagerState,
    onNextClick: () -> Unit,
    onDocumentDownloadClick: () -> Unit,
    onBindingDirectionChange: (BindingDirection) -> Unit,
) {
    Scaffold(
        bottomBar = { TutorialBottomBar(pageState, onNextClick) },
        contentWindowInsets = WindowInsets.safeDrawing
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
    LocalMinimumInteractiveComponentSize
}

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

internal val PagerState.isLastPage: Boolean
    get() = currentPage == pageCount - 1
