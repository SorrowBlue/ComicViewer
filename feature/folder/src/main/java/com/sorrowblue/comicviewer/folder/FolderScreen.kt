package com.sorrowblue.comicviewer.folder

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneAdaptedValue
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.SavedStateHandle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.folder.navigation.FolderArgs
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.SortItem
import com.sorrowblue.comicviewer.folder.section.SortOrder
import com.sorrowblue.comicviewer.folder.section.SortSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.CoreNavigator
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import kotlin.math.min
import kotlinx.parcelize.Parcelize
import logcat.asLog
import logcat.logcat

@Parcelize
internal data class FolderScreenUiState(
    var file: File? = null,
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val openSortSheet: Boolean = false,
    val sortItem: SortItem = SortItem.Name,
    val sortOrder: SortOrder = SortOrder.Asc,
    val fileContentType: FileContentType = FileContentType.Grid(),
) : Parcelable

interface FolderScreenNavigator : CoreNavigator {
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
    fun onFileClick(file: File)
    fun onFavoriteClick(file: File)
}

@Composable
fun FolderScreen(
    args: FolderArgs,
    savedStateHandle: SavedStateHandle,
    navigator: FolderScreenNavigator,
    onRestoreComplete: () -> Unit = {},
) {
    FolderScreen(
        args = args,
        savedStateHandle = savedStateHandle,
        onBackClick = navigator::navigateUp,
        onSearchClick = navigator::onSearchClick,
        onSettingsClick = navigator::onSettingsClick,
        onFileClick = navigator::onFileClick,
        onFavoriteClick = navigator::onFavoriteClick,
        onRestoreComplete = onRestoreComplete
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun FolderScreen(
    args: FolderArgs,
    savedStateHandle: SavedStateHandle,
    onBackClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onRestoreComplete: () -> Unit = {},
) {
    FolderScreen(
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onSettingsClick = onSettingsClick,
        onFileClick = onFileClick,
        onFavoriteClick = onFavoriteClick,
        state = rememberFolderScreenState(args = args, savedStateHandle = savedStateHandle),
        onRestoreComplete = onRestoreComplete
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FolderScreen(
    onBackClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    state: FolderScreenState,
    onRestoreComplete: () -> Unit = {},
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val uiState = state.uiState
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            // fetch something
            lazyPagingItems.refresh()
        }
    }
    FolderScreen(
        uiState = uiState,
        navigator = state.navigator,
        lazyPagingItems = lazyPagingItems,
        onSearchClick = { onSearchClick(state.bookshelfId, state.path) },
        onFileClick = onFileClick,
        onFileInfoClick = state::onFileInfoClick,
        lazyGridState = state.lazyGridState,
        pullRefreshState = pullRefreshState,
        onFileListChange = state::toggleFileListType,
        onSettingsClick = onSettingsClick,
        onGridSizeChange = state::onGridSizeChange,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onBackClick = onBackClick,
        onSortClick = state::openSort,
        onSortItemClick = state::onSortItemClick,
        onSortOrderClick = state::onSortOrderClick,
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick
    )

    if (uiState.openSortSheet) {
        SortSheet(
            currentSortItem = uiState.sortItem,
            currentSortOrder = uiState.sortOrder,
            onSortItemClick = state::onSortItemClick,
            onSortOrderClick = state::onSortOrderClick,
            onDismissRequest = state::onSortSheetDismissRequest
        )
    }
    LaunchedEffect(lazyPagingItems.loadState) {
        if (0 < lazyPagingItems.itemCount && state.restorePath != null) {
            val index = lazyPagingItems.indexOf { it?.path == state.restorePath }
            if (0 <= index) {
                state.restorePath = null
                runCatching {
                    state.lazyGridState.scrollToItem(min(index, lazyPagingItems.itemCount - 1))
                }.onFailure {
                    logcat { it.asLog() }
                }
                onRestoreComplete()
            } else if (!lazyPagingItems.loadState.isLoading) {
                onRestoreComplete()
            }
        }
        if (lazyPagingItems.isLoadedData) {
            pullRefreshState.endRefresh()
        }
        if (lazyPagingItems.isLoadedData && state.isScrollableTop) {
            state.isScrollableTop = false
            state.lazyGridState.scrollToItem(0)
        }
    }

    val sort by state.sort.collectAsState()
    LaunchedEffect(sort) {
        if (!state.isSkipFirstRefresh) {
            lazyPagingItems.refresh()
        }
    }

    BackHandler(enabled = state.navigator.scaffoldState.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
        state.navigator.navigateBack()
    }

    NavTabHandler(onClick = state::onNavClick)
}

val CombinedLoadStates.isLoading
    get() =
        source.any { it == LoadState.Loading } || mediator?.any { it == LoadState.Loading } ?: false

fun LoadStates.any(op: (LoadState) -> Boolean): Boolean {
    return op(refresh) || op(append) || op(prepend)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FolderScreen(
    navigator: ThreePaneScaffoldNavigator,
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onSearchClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    lazyGridState: LazyGridState,
    pullRefreshState: PullToRefreshState,
    onFileListChange: () -> Unit,
    onSettingsClick: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onGridSizeChange: () -> Unit,
    onBackClick: () -> Unit,
    onSortClick: () -> Unit,
    onSortItemClick: (SortItem) -> Unit,
    onSortOrderClick: (SortOrder) -> Unit,
    onReadLaterClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            FolderAppBar(
                uiState = uiState.folderAppBarUiState,
                onFileListChange = onFileListChange,
                onBackClick = onBackClick,
                onSearchClick = onSearchClick,
                onGridSizeChange = onGridSizeChange,
                onSortClick = onSortClick,
                onSortItemClick = onSortItemClick,
                onSortOrderClick = onSortOrderClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior,
            )
        },
        extraPane = { innerPadding ->
            uiState.file?.let { file ->
                FileInfoSheet(
                    file = file,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = { onReadLaterClick(file) },
                    onFavoriteClick = { onFavoriteClick(file) },
                    onOpenFolderClick = null,
                    contentPadding = innerPadding
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        val scaleFraction = if (pullRefreshState.isRefreshing) {
            1f
        } else {
            LinearOutSlowInEasing.transform(pullRefreshState.progress).coerceIn(0f, 1f)
        }
        Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
            if (lazyPagingItems.isEmptyData) {
                EmptyContent(
                    imageVector = ComicIcons.UndrawResumeFolder,
                    text = stringResource(
                        R.string.folder_text_nothing_in_folder,
                        uiState.folderAppBarUiState.title
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else {
                FileContent(
                    type = uiState.fileContentType,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = innerPadding,
                    onFileClick = onFileClick,
                    onInfoClick = onFileInfoClick,
                    state = lazyGridState
                )
            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier
                    .padding(innerPadding)
                    .align(Alignment.TopCenter)
                    .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
            )
        }
    }
}
