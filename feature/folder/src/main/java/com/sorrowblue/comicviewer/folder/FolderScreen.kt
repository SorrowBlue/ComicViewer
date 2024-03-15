package com.sorrowblue.comicviewer.folder

import android.os.Parcelable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.fakeBookFile
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.rememberFileContentType
import com.sorrowblue.comicviewer.file.rememberThreePaneScaffoldNavigatorContent
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.SortSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.calculatePaddingMargins
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.adaptive.navigation.BackHandlerForNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlin.math.min
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.parcelize.Parcelize
import logcat.asLog
import logcat.logcat

class FolderArgs(
    val bookshelfId: BookshelfId,
    val path: String,
    val restorePath: String?,
)

@Parcelize
internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val display: FolderDisplaySettings.Display = FolderDisplaySettings.Display.List,
    val columnSize: FolderDisplaySettings.ColumnSize = FolderDisplaySettings.ColumnSize.Medium,
    val isThumbnailEnabled: Boolean = true,
) : Parcelable

interface FolderScreenNavigator {
    fun navigateUp()
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
    fun onFileClick(file: File)
    fun onFavoriteClick(file: File)
}

@Composable
fun FolderScreen(
    args: FolderArgs,
    navigator: FolderScreenNavigator,
    onRestoreComplete: () -> Unit = {},
) {
    FolderScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onSearchClick = navigator::onSearchClick,
        onSettingsClick = navigator::onSettingsClick,
        onFileClick = navigator::onFileClick,
        onFavoriteClick = navigator::onFavoriteClick,
        onRestoreComplete = onRestoreComplete
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    args: FolderArgs,
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
        state = rememberFolderScreenState(args = args),
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
    FolderScreen(
        navigator = state.navigator,
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onSearchClick = { onSearchClick(state.bookshelfId, state.path) },
        onFileClick = onFileClick,
        onFileInfoClick = state::onFileInfoClick,
        lazyGridState = state.lazyGridState,
        pullRefreshState = state.pullRefreshState,
        snackbarHostState = state.snackbarHostState,
        onFileListChange = state::toggleFileListType,
        onHideFileClick = state::onHideFileClick,
        onSettingsClick = onSettingsClick,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onGridSizeChange = state::onGridSizeChange,
        onBackClick = onBackClick,
        onSortClick = state::openSortSheet,
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
    )

    SortSheet(
        uiState = state.sortSheetUiState,
        onSortItemClick = state::onSortItemClick,
        onSortOrderClick = state::onSortOrderClick,
        onDismissRequest = state::onSortSheetDismissRequest
    )

    if (state.isScrollableTop) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ComicTheme.colorScheme.scrim.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    var isManualOperation by remember { mutableStateOf(false) }

    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.loadState.isNotLoading) {
            state.pullRefreshState.endRefresh()
        }
        if (lazyPagingItems.loadState.isLoading) {
            isManualOperation = true
            state.pullRefreshState.startRefresh()
        }
    }
    LaunchedEffect(state.pullRefreshState.isRefreshing) {
        logcat("APPAPP") { "state.pullRefreshState.isRefreshing=${state.pullRefreshState.isRefreshing}" }
        if (state.pullRefreshState.isRefreshing) {
            if (isManualOperation) {
                isManualOperation = false
            } else {
                lazyPagingItems.refresh()
            }
        } else {
            isManualOperation = false
        }
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
        if (state.isScrollableTop) {
            state.lazyGridState.scrollToItem(0)
            state.isScrollableTop = false
        }

        if (lazyPagingItems.loadState.append is LoadState.Error) {
            ((lazyPagingItems.loadState.append as LoadState.Error).error as? PagingException)?.let {
                when (it) {
                    PagingException.InvalidAuth -> {
                        state.snackbarHostState.showSnackbar("認証エラー")
                    }

                    PagingException.InvalidServer -> {
                        state.snackbarHostState.showSnackbar("サーバーエラー")
                    }

                    PagingException.NoNetwork -> {
                        state.snackbarHostState.showSnackbar("ネットワークエラー")
                    }

                    PagingException.NotFound -> {
                        state.snackbarHostState.showSnackbar("見つかりませんでした")
                    }
                }
            }
        }
    }

    val sort by state.sort.collectAsState()
    LaunchedEffect(sort) {
        if (!state.isSkipFirstRefresh) {
            lazyPagingItems.refresh()
        }
    }

    val showHidden by state.showHidden.collectAsState()
    LaunchedEffect(showHidden) {
        if (!state.isSkipFirstRefresh) {
            lazyPagingItems.refresh()
        }
    }

    BackHandlerForNavigator(navigator = state.navigator)

    NavTabHandler(onClick = state::onNavClick)
}

val CombinedLoadStates.isLoading
    get() = source.refresh is LoadState.Loading || (mediator == null || mediator!!.refresh is LoadState.Loading)

val CombinedLoadStates.isNotLoading
    get() = source.refresh is LoadState.NotLoading && (mediator?.refresh is LoadState.NotLoading)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FolderScreen(
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onSearchClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    lazyGridState: LazyGridState,
    pullRefreshState: PullToRefreshState,
    snackbarHostState: SnackbarHostState,
    onFileListChange: () -> Unit,
    onHideFileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onGridSizeChange: () -> Unit,
    onBackClick: () -> Unit,
    onSortClick: () -> Unit,
    onReadLaterClick: () -> Unit,
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
                onHideFileClick = onHideFileClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior,
            )
        },
        extraPane = { innerPadding ->
            val fileInfo by rememberThreePaneScaffoldNavigatorContent(navigator)
            fileInfo?.let {
                FileInfoSheet(
                    fileInfoUiState = it,
                    scaffoldDirective = navigator.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = onReadLaterClick,
                    onFavoriteClick = { onFavoriteClick(it.file) },
                    onOpenFolderClick = null,
                    contentPadding = innerPadding
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            LaunchedEffect(lazyPagingItems.loadState) {
                val loadState = lazyPagingItems.loadState
                logcat { "\t${loadState.joinToString { "\t${it?.javaClass?.simpleName}\t${it?.endOfPaginationReached}" }}\t" }
            }
            var progress by remember {
                mutableFloatStateOf(0f)
            }
            LaunchedEffect(pullRefreshState.progress) {
                if (pullRefreshState.progress == 0f) {
                    animate(progress, 0f) { value, _ ->
                        progress = value
                    }
                } else {
                    progress = pullRefreshState.progress
                }
            }
            if (lazyPagingItems.isEmptyData) {
                EmptyContent(
                    imageVector = ComicIcons.UndrawResumeFolder,
                    text = stringResource(
                        R.string.folder_text_nothing_in_folder,
                        uiState.folderAppBarUiState.title
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )
            } else {
                val contentType by rememberFileContentType(uiState.display, uiState.columnSize)
                val (paddings, margins) = calculatePaddingMargins(contentPadding)
                FileLazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(margins),
                    isThumbnailEnabled = uiState.isThumbnailEnabled,
                    contentType = contentType,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = paddings,
                    onItemClick = onFileClick,
                    onItemInfoClick = onFileInfoClick,
                    state = lazyGridState
                )
            }
            if (pullRefreshState.isRefreshing) {
                LinearProgressIndicator(
                    trackColor = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding)
                )
            } else {
                LinearProgressIndicator(
                    progress = { progress },
                    trackColor = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(contentPadding)
                )
            }
        }
    }
}

fun CombinedLoadStates.joinToString(action: (LoadState?) -> String): String {
    return action(refresh) +
//        action(prepend) +
//        action(append) +
        action(source.refresh) +
//        action(source.prepend) +
//        action(source.append) +
        action(mediator?.refresh)
//        action(mediator?.prepend) +
//        action(mediator?.append)
}

@Composable
@Preview
@OptIn(ExperimentalMaterial3Api::class)
fun PullToRefreshLinearProgressIndicatorSample() {
    var itemCount by remember { mutableStateOf(15) }
    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            // fetch something
            delay(1500)
            itemCount += 5
            state.endRefresh()
        }
    }
    Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (!state.isRefreshing) {
                items(itemCount) {
                    ListItem({ Text(text = "Item ${itemCount - it}") })
                }
            }
        }
        if (state.isRefreshing) {
            LinearProgressIndicator()
        } else {
            LinearProgressIndicator(progress = { state.progress })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshWrapContainer(
    pullRefreshState: PullToRefreshState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val scaleFraction =
        if (pullRefreshState.isRefreshing) 1f
        else LinearOutSlowInEasing.transform(pullRefreshState.progress).coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
    ) {
        content()
        PullToRefreshContainer(
            modifier = Modifier
                .padding(contentPadding)
                .align(Alignment.TopCenter)
                .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
            state = pullRefreshState,
        )
    }
}

@ExperimentalMaterial3Api
@ExperimentalMaterial3AdaptiveApi
@Preview
@Composable
private fun PreviewFolderScreen() {
    val files = List(20) {
        fakeBookFile(BookshelfId(it))
    }
    val pagingDataFlow: Flow<PagingData<File>> = flowOf(PagingData.from(files))
    PreviewTheme {
        FolderScreen(
            navigator = rememberSupportingPaneScaffoldNavigator(),
            uiState = FolderScreenUiState(),
            lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems(),
            onSearchClick = {},
            onFileClick = {},
            onFileInfoClick = {},
            lazyGridState = rememberLazyGridState(),
            pullRefreshState = rememberPullToRefreshState(),
            snackbarHostState = remember {
                SnackbarHostState()
            },
            onFileListChange = {},
            onHideFileClick = {},
            onSettingsClick = {},
            onExtraPaneCloseClick = {},
            onGridSizeChange = {},
            onBackClick = {},
            onSortClick = {},
            onReadLaterClick = {},
            onFavoriteClick = {},
        )
    }
}

@ExperimentalMaterial3Api
@ExperimentalMaterial3AdaptiveApi
@Preview
@Composable
private fun PreviewFolderScreenEmpty() {
    val pagingDataFlow: Flow<PagingData<File>> = flowOf(
        PagingData.empty(
            sourceLoadStates =
            LoadStates(
                refresh = LoadState.NotLoading(true),
                append = LoadState.NotLoading(true),
                prepend = LoadState.NotLoading(true),
            ),
        )
    )
    PreviewTheme {
        FolderScreen(
            navigator = rememberSupportingPaneScaffoldNavigator(),
            uiState = FolderScreenUiState(),
            lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems(),
            onSearchClick = {},
            onFileClick = {},
            onFileInfoClick = {},
            lazyGridState = rememberLazyGridState(),
            pullRefreshState = rememberPullToRefreshState(),
            snackbarHostState = remember {
                SnackbarHostState()
            },
            onFileListChange = {},
            onHideFileClick = {},
            onSettingsClick = {},
            onExtraPaneCloseClick = {},
            onGridSizeChange = {},
            onBackClick = {},
            onSortClick = {},
            onReadLaterClick = {},
            onFavoriteClick = {},
        )
    }
}
