package com.sorrowblue.comicviewer.folder

import android.os.Parcelable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.result.ResultRecipient
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.feature.folder.destinations.SortTypeDialogDestination
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderFab
import com.sorrowblue.comicviewer.folder.section.FolderFabAction
import com.sorrowblue.comicviewer.folder.section.FolderTopAppBarAction
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.material3.LinearPullRefreshContainer
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.flowEmptyData
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FolderScreenUiState(
    val bookshelfId: BookshelfId = BookshelfId(),
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
    val sortType: SortType = FolderDisplaySettingsDefaults.sortType,
) : Parcelable

@Composable
fun FolderScreen(
    args: FolderArgs,
    navigator: FolderScreenNavigator,
    sortTypeResultRecipient: ResultRecipient<SortTypeDialogDestination, SortType>,
) {
    val state = rememberFolderScreenState(args = args)
    FolderScreen(
        uiState = state.uiState,
        navigator = state.navigator,
        lazyPagingItems = state.lazyPagingItems,
        onFolderTopAppBarAction = state::onFolderTopAppBarAction,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
        onFolderContentsAction = state::onFolderContentsAction,
        onFolderFabAction = state::onFolderFabAction,
        lazyGridState = state.lazyGridState,
        pullRefreshState = state.pullRefreshState,
        snackbarHostState = state.snackbarHostState,
    )

    val currentNavigator by rememberUpdatedState(navigator)
    LaunchedEventEffect(state.event) {
        when (it) {
            FolderScreenEvent.Back -> currentNavigator.navigateUp()
            is FolderScreenEvent.Favorite -> currentNavigator.onFavoriteClick(it.file)
            is FolderScreenEvent.File -> currentNavigator.onFileClick(it.file)
            FolderScreenEvent.Restore -> currentNavigator.onRestoreComplete()
            is FolderScreenEvent.Search -> currentNavigator.onSearchClick(it.bookshelfId, it.path)
            FolderScreenEvent.Settings -> currentNavigator.onSettingsClick()
            is FolderScreenEvent.Sort -> currentNavigator.onSortClick(it.sortType)
        }
    }

    LaunchedEffect(state.lazyPagingItems.loadState) {
        state.onLoadStateChange(state.lazyPagingItems)
    }

    sortTypeResultRecipient.onNavResult(state::onNavResult)

    NavTabHandler(onClick = state::onNavClick)
}

@Composable
private fun FolderScreen(
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onFolderTopAppBarAction: (FolderTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetAction) -> Unit,
    onFolderContentsAction: (FolderContentsAction) -> Unit,
    onFolderFabAction: (FolderFabAction) -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    pullRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            FolderAppBar(
                uiState = uiState.folderAppBarUiState,
                onAction = onFolderTopAppBarAction,
                scrollBehavior = scrollBehavior,
            )
        },
        extraPane = { contentPadding, fileInfoUiState ->
            FileInfoSheet(
                uiState = fileInfoUiState,
                onAction = onFileInfoSheetAction,
                contentPadding = contentPadding
            )
        },
        floatingActionButton = {
            FolderFab(
                scrollTop = lazyGridState.canScrollBackward,
                scrollDown = lazyGridState.canScrollForward,
                onAction = onFolderFabAction,
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        FolderContents(
            title = uiState.folderAppBarUiState.title,
            fileLazyVerticalGridUiState = uiState.fileLazyVerticalGridUiState,
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            pullRefreshState = pullRefreshState,
            onAction = onFolderContentsAction,
            contentPadding = contentPadding
        )
    }
}

internal sealed interface FolderContentsAction {

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FolderContentsAction

    data class FileInfo(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FolderContentsAction

    data object Refresh : FolderContentsAction
}

@Composable
private fun FolderContents(
    title: String,
    fileLazyVerticalGridUiState: FileLazyVerticalGridUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    pullRefreshState: PullToRefreshState,
    onAction: (FolderContentsAction) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val isRefreshing by remember(lazyPagingItems.loadState) { derivedStateOf { lazyPagingItems.loadState.isLoading } }
    LinearPullRefreshContainer(
        pullRefreshState = pullRefreshState,
        contentPadding = contentPadding,
        isRefreshing = isRefreshing,
        onRefresh = { onAction(FolderContentsAction.Refresh) },
        modifier = modifier
    ) {
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding),
                imageVector = ComicIcons.UndrawResumeFolder,
                text = stringResource(R.string.folder_text_nothing_in_folder, title)
            )
        } else {
            FileLazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize(),
                uiState = fileLazyVerticalGridUiState,
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                onItemClick = { onAction(FolderContentsAction.File(it)) },
                onItemInfoClick = { onAction(FolderContentsAction.FileInfo(it)) },
                state = lazyGridState
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFolderScreen() {
    val pagingDataFlow = PagingData.flowData<File> {
        fakeBookFile(it)
    }
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
    PreviewTheme {
        FolderScreen(
            navigator = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
            uiState = FolderScreenUiState(folderAppBarUiState = FolderAppBarUiState("Preview title")),
            lazyPagingItems = lazyPagingItems,
            onFileInfoSheetAction = {},
            onFolderTopAppBarAction = {},
            onFolderContentsAction = {},
            onFolderFabAction = {}
        )
    }
}

@Preview
@Composable
private fun PreviewFolderScreenEmpty() {
    val pagingDataFlow = PagingData.flowEmptyData<File>()
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
    PreviewTheme {
        FolderScreen(
            navigator = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
            uiState = FolderScreenUiState(folderAppBarUiState = FolderAppBarUiState("Preview title")),
            lazyPagingItems = lazyPagingItems,
            onFileInfoSheetAction = {},
            onFolderTopAppBarAction = {},
            onFolderContentsAction = {},
            onFolderFabAction = {}
        )
    }
}
