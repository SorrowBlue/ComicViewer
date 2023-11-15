package com.sorrowblue.comicviewer.feature.tutorial

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.tutorial.section.ArchiveSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DirectionSheetUiState
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheet
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import com.sorrowblue.comicviewer.feature.tutorial.section.WelcomeSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.coroutines.launch

internal enum class TutorialSheet {
    WELCOME,
    ARCHIVE,
    DOCUMENT,
    READING_DIRECTION,
}

internal data class TutorialScreenUiState(
    val list: List<TutorialSheet> = TutorialSheet.entries,
    val documentSheetUiState: DocumentSheetUiState = DocumentSheetUiState.NONE,
    val directionSheetUiState: DirectionSheetUiState = DirectionSheetUiState(),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TutorialRoute(
    onComplete: () -> Unit,
    viewModel: TutorialViewModel = hiltViewModel(),
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, viewModel) {
        lifecycle.addObserver(viewModel)
        onDispose { lifecycle.removeObserver(viewModel) }
    }
    val uiState by viewModel.uiState.collectAsState()
    val pageState = rememberPagerState { uiState.list.size }
    val scope = rememberCoroutineScope()
    TutorialScreen(
        uiState = uiState,
        pageState = pageState,
        onNextClick = {
            if (pageState.isLastPage) {
                onComplete()
            } else {
                scope.launch {
                    pageState.animateScrollToPage(pageState.currentPage + 1)
                }
            }
        },
        onDocumentDownloadClick = viewModel::onDocumentDownloadClick,
        onBindingDirectionChange = viewModel::updateReadingDirection
    )
    BackHandler(pageState.currentPage != 0) {
        scope.launch {
            pageState.animateScrollToPage(pageState.currentPage - 1)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TutorialScreen(
    uiState: TutorialScreenUiState = TutorialScreenUiState(),
    pageState: PagerState = rememberPagerState { uiState.list.size },
    onNextClick: () -> Unit = {},
    onDocumentDownloadClick: () -> Unit = {},
    onBindingDirectionChange: (BindingDirection) -> Unit = {},
) {
    Surface {
        Box {
            HorizontalPager(state = pageState) {
                when (uiState.list[it]) {
                    TutorialSheet.WELCOME -> WelcomeSheet()
                    TutorialSheet.ARCHIVE -> ArchiveSheet()
                    TutorialSheet.DOCUMENT -> DocumentSheet(
                        uiState = uiState.documentSheetUiState,
                        onDownloadClick = onDocumentDownloadClick
                    )

                    TutorialSheet.READING_DIRECTION -> DirectionSheet(
                        uiState = uiState.directionSheetUiState,
                        onBindingDirectionChange = onBindingDirectionChange,
                    )
                }
            }
            HorizontalPagerIndicator(
                pagerState = pageState,
                activeColor = MaterialTheme.colorScheme.primary,
                pageCount = TutorialSheet.entries.size,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(
                        start = 16.dp,
                        bottom = 16.dp
                    )
                    .align(Alignment.BottomStart)
            )

            val isLastPage = remember(pageState.isLastPage) { pageState.isLastPage }
            IconButton(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .align(Alignment.BottomEnd),
                onClick = onNextClick
            ) {
                if (isLastPage) {
                    Icon(ComicIcons.Done, contentDescription = "Done")
                } else {
                    Icon(ComicIcons.ArrowRight, contentDescription = "Next")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun PreviewTutorialScreen() {
    ComicTheme {
        Surface {
            TutorialScreen()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private val PagerState.isLastPage: Boolean
    get() = currentPage == pageCount - 1
