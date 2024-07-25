package com.sorrowblue.comicviewer.folder

import android.os.Parcelable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
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
import com.sorrowblue.comicviewer.folder.section.FolderTopAppBarAction
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.preview.flowData
import com.sorrowblue.comicviewer.framework.preview.flowEmptyData
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.calculatePaddingMargins
import com.sorrowblue.comicviewer.framework.ui.material3.LinearPullRefreshContainer
import com.sorrowblue.comicviewer.framework.ui.material3.adaptive.navigation.BackHandlerForNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
    val sortType: SortType = FolderDisplaySettingsDefaults.sortType,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FolderScreen(
    args: FolderArgs,
    navigator: FolderScreenNavigator,
    sortTypeResultRecipient: ResultRecipient<SortTypeDialogDestination, SortType>,
) {
    val state = rememberFolderScreenState(args = args)
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FolderScreen(
        uiState = state.uiState,
        navigator = state.navigator,
        lazyPagingItems = lazyPagingItems,
        onFolderTopAppBarAction = state::onFolderTopAppBarAction,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
        onFolderContentsAction = state::onFolderContentsAction,
        lazyGridState = state.lazyGridState,
        pullRefreshState = state.pullRefreshState,
        snackbarHostState = state.snackbarHostState,
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        state.event.flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.RESUMED).onEach {
            when (it) {
                FolderScreenEvent.Back -> navigator.navigateUp()
                is FolderScreenEvent.Favorite -> navigator.onFavoriteClick(it.file)
                is FolderScreenEvent.File -> navigator.onFileClick(it.file)
                FolderScreenEvent.Refresh -> lazyPagingItems.refresh()
                FolderScreenEvent.Restore -> navigator.onRestoreComplete()
                is FolderScreenEvent.Search -> navigator.onSearchClick(it.bookshelfId, it.path)
                FolderScreenEvent.Settings -> navigator.onSettingsClick()
                is FolderScreenEvent.Sort -> navigator.onSortClick(it.sortType)
            }
        }
            .launchIn(this)
    }

    LaunchedEffect(lazyPagingItems.loadState) {
        state.onLoadStateChange(lazyPagingItems)
    }

    sortTypeResultRecipient.onNavResult(state::onNavResult)

    BackHandlerForNavigator(navigator = state.navigator)

    NavTabHandler(onClick = state::onNavClick)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun FolderScreen(
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onFolderTopAppBarAction: (FolderTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetAction) -> Unit,
    onFolderContentsAction: (FolderContentsAction) -> Unit,
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
        extraPaneVisible = { innerPadding, fileInfoUiState ->
            FileInfoSheet(
                uiState = fileInfoUiState,
                scaffoldDirective = navigator.scaffoldDirective,
                onAction = onFileInfoSheetAction,
                contentPadding = innerPadding
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

sealed interface FolderContentsAction {

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FolderContentsAction

    data class FileInfo(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FolderContentsAction

    data object Refresh : FolderContentsAction
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderContents(
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
            val (paddings, margins) = calculatePaddingMargins(contentPadding)
            FileLazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(margins),
                uiState = fileLazyVerticalGridUiState,
                lazyPagingItems = lazyPagingItems,
                contentPadding = paddings,
                onItemClick = { onAction(FolderContentsAction.File(it)) },
                onItemInfoClick = { onAction(FolderContentsAction.FileInfo(it)) },
                state = lazyGridState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewFolderScreen() {
    val pagingDataFlow = PagingData.flowData<File> {
        fakeBookFile(BookshelfId(it))
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
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
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
        )
    }
}
