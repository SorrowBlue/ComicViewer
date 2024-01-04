package com.sorrowblue.comicviewer.feature.history

import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.section.HistoryAppBar
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlinx.parcelize.Parcelize

interface HistoryScreenNavigator : CoreNavigator {
    fun onSettingsClick()
    fun navigateToBook(book: Book)
    fun onFavoriteClick(file: File)
}

@Destination
@Composable
internal fun HistoryScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: HistoryScreenNavigator,
) {
    HistoryScreen(
        savedStateHandle = navBackStackEntry.savedStateHandle,
        onBackClick = navigator::navigateUp,
        onSettingsClick = navigator::onSettingsClick,
        onFileClick = navigator::navigateToBook,
        onFavoriteClick = navigator::onFavoriteClick
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun HistoryScreen(
    savedStateHandle: SavedStateHandle,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    state: HistoryScreenState = rememberHistoryScreenState(savedStateHandle = savedStateHandle),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val uiState = state.uiState
    val lazyGridState = rememberLazyGridState()
    HistoryScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        navigator = state.navigator,
        onBackClick = onBackClick,
        onFileClick = onFileClick,
        onFileInfoClick = state::onFileInfoClick,
        onSettingsClick = onSettingsClick,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
        lazyGridState = lazyGridState,
    )
}

@Parcelize
data class HistoryScreenUiState(
    val file: File? = null,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun HistoryScreen(
    uiState: HistoryScreenUiState,
    lazyPagingItems: LazyPagingItems<Book>,
    navigator: ThreePaneScaffoldNavigator,
    onFileClick: (Book) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onReadLaterClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    lazyGridState: LazyGridState,
    onBackClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            HistoryAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior,
                onSettingsClick = onSettingsClick,
            )
        },
        extraPane = { innerPadding ->
            if (uiState.file != null) {
                FileInfoSheet(
                    file = uiState.file,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = { onReadLaterClick(uiState.file) },
                    onFavoriteClick = { onFavoriteClick(uiState.file) },
                    contentPadding = innerPadding
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawResumeFolder,
                text = stringResource(id = R.string.history_label_no_history),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            FileContent(
                type = FileContentType.List,
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding,
                onFileClick = onFileClick,
                onInfoClick = onFileInfoClick,
                state = lazyGridState
            )
        }
    }
}
