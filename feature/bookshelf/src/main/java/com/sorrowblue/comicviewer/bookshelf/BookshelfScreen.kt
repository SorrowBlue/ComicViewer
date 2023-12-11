package com.sorrowblue.comicviewer.bookshelf

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneAdaptedValue
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfMainSheet
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialog
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfSideSheet
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.animation.topAppBarAnimation
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.PreviewComic
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.copy
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewScreen
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Text
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun BookshelfRoute(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    state: BookshelfScreenState = rememberBookshelfScreenState(),
) {
    BookshelfScreen(
        navigator = state.navigator,
        bookshelfFolder = state.bookshelfFolder,
        lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems(),
        snackbarHostState = state.snackbarHostState,
        contentPadding = contentPadding,
        onFabClick = onFabClick,
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onBookshelfInfoClick = state::onBookshelfLongClick,
        onInfoSheetRemoveClick = state::onRemoveClick,
        onInfoSheetEditClick = { onEditClick(state.bookshelfId) },
        onInfoSheetCloseClick = state::onInfoSheetCloseClick,
        onInfoSheetScanClick = state::onInfoSheetScanClick,
    )
    val removeDialogController = state.removeDialogController
    if (removeDialogController.isShow) {
        BookshelfRemoveDialog(
            title = removeDialogController.value!!.bookshelf.displayName,
            onDismissRequest = state::onDismissRequest,
            onConfirmClick = state::onConfirmClick,
        )
    }

    BackHandler(enabled = state.navigator.scaffoldState.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
        state.navigator.navigateBack()
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfScreen(
    navigator: ThreePaneScaffoldNavigator<SupportingPaneScaffoldRole>,
    bookshelfFolder: BookshelfFolder?,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    onInfoSheetRemoveClick: () -> Unit,
    onInfoSheetEditClick: () -> Unit,
    onInfoSheetScanClick: () -> Unit,
    onInfoSheetCloseClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyGridState = rememberLazyStaggeredGridState()

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = navigator.scaffoldState.scaffoldDirective.maxHorizontalPartitions != 1 ||
                        navigator.scaffoldState.scaffoldValue.tertiary == PaneAdaptedValue.Hidden,
                transitionSpec = { topAppBarAnimation() },
                contentAlignment = Alignment.TopCenter,
                label = "top_app_bar"
            ) {
                if (it) {
                    TopAppBar(
                        title = {
                            Text(text = stringResource(id = R.string.bookshelf_list_title))
                        },
                        actions = {
                            IconButton(onClick = onSettingsClick) {
                                Icon(imageVector = ComicIcons.Settings, contentDescription = null)
                            }
                        },
                        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                        scrollBehavior = scrollBehavior,
                    )
                } else {
                    Spacer(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        floatingActionButton = {
            val expanded by remember(lazyGridState) {
                derivedStateOf { !lazyGridState.canScrollForward || !lazyGridState.canScrollBackward }
            }
            if (navigator.scaffoldState.scaffoldValue.tertiary == PaneAdaptedValue.Hidden) {
                ExtendedFloatingActionButton(
                    expanded = expanded,
                    onClick = onFabClick,
                    text = { Text(text = "Add") },
                    icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.End
                        )
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = contentPadding.asWindowInsets(),
        containerColor = ComicTheme.colorScheme.surface,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        ExtraPaneScaffold(
            navigator = navigator,
            extraPane = {
                if (bookshelfFolder != null) {
                    BookshelfSideSheet(
                        contentPadding = innerPadding,
                        scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                        bookshelfFolder = bookshelfFolder,
                        onRemoveClick = onInfoSheetRemoveClick,
                        onEditClick = onInfoSheetEditClick,
                        onScanClick = onInfoSheetScanClick,
                        onCloseClick = onInfoSheetCloseClick
                    )
                }
            },
        ) {
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            val end by animateDpAsState(
                targetValue = if (navigator.scaffoldState.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
                    0.dp
                } else if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
                    16.dp + innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                } else {
                    24.dp + innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                },
                label = "end"
            )
            val innerPadding = innerPadding.copy(end = end).add(
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact ->
                        PaddingValues(start = 16.dp, top = 16.dp, bottom = 16.dp + FabSpace)

                    WindowWidthSizeClass.Medium ->
                        PaddingValues(start = 24.dp, top = 24.dp, bottom = 24.dp + FabSpace)

                    WindowWidthSizeClass.Expanded ->
                        PaddingValues(start = 24.dp, top = 24.dp, bottom = 24.dp + FabSpace)

                    else -> PaddingValues()
                }
            )
            BookshelfMainSheet(
                lazyPagingItems,
                innerPadding,
                lazyGridState,
                onBookshelfClick,
                onBookshelfInfoClick
            )
        }
    }
}

private val FabSpace get() = 72.dp

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@PreviewComic
@Composable
private fun PreviewBookshelfScreen() {
    PreviewTheme {
        val pagingDataFlow = remember {
            List(15) {
                BookshelfFolder(
                    InternalStorage(BookshelfId(it), "name"),
                    Folder(
                        BookshelfId(it),
                        "path",
                        "name",
                        "name",
                        0L,
                        0,
                        emptyMap(),
                        0,
                        0,
                    )
                )
            }.toPersistentList().let {
                MutableStateFlow(PagingData.from(it))
            }
        }
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        PreviewScreen {
            BookshelfScreen(
                snackbarHostState = remember { SnackbarHostState() },
                navigator = rememberSupportingPaneScaffoldNavigator(),
                bookshelfFolder = BookshelfFolder(
                    InternalStorage(BookshelfId(0), "display name", 0),
                    Folder(
                        BookshelfId(0),
                        "",
                        "",
                        "",
                        0,
                        0
                    )
                ),
                lazyPagingItems = lazyPagingItems,
                contentPadding = PaddingValues(),
                onFabClick = {},
                onSettingsClick = {},
                onBookshelfClick = { _, _ -> },
                onBookshelfInfoClick = {},
                onInfoSheetRemoveClick = {},
                onInfoSheetEditClick = {},
                onInfoSheetScanClick = {},
                onInfoSheetCloseClick = {},
            )
        }
    }
}
