package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterGraphTransitions
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterAppBar
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.file.rememberThreePaneScaffoldNavigatorContent
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawSaveBookmarks
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.calculatePaddingMargins
import com.sorrowblue.comicviewer.framework.ui.material3.adaptive.navigation.BackHandlerForNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

interface ReadLaterScreenNavigator {
    fun onSettingsClick()
    fun onFileClick(file: File)
    fun onFavoriteClick(file: File)
    fun onOpenFolderClick(file: File)
}

@Destination<ReadLaterGraph>(
    start = true,
    style = ReadLaterGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun ReadLaterScreen(navigator: ReadLaterScreenNavigator) {
    ReadLaterScreen(
        onSettingsClick = navigator::onSettingsClick,
        onFileClick = navigator::onFileClick,
        onFavoriteClick = navigator::onFavoriteClick,
        onOpenFolderClick = navigator::onOpenFolderClick
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ReadLaterScreen(
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    state: ReadLaterScreenState = rememberReadLaterScreenState(),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    ReadLaterScreen(
        lazyPagingItems = lazyPagingItems,
        navigator = state.navigator,
        lazyGridState = state.lazyGridState,
        onFileClick = onFileClick,
        onFileInfoClick = state::onFileInfoClick,
        onSettingsClick = onSettingsClick,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
        onOpenFolderClick = onOpenFolderClick,
        onClearAllClick = state::onClearAllClick,
    )

    NavTabHandler(onClick = state::onNavClick)

    BackHandlerForNavigator(navigator = state.navigator)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ReadLaterScreen(
    lazyPagingItems: LazyPagingItems<File>,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    lazyGridState: LazyGridState,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onClearAllClick: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            ReadLaterAppBar(
                onClearAllClick = onClearAllClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior
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
                    onOpenFolderClick = { onOpenFolderClick(it.file) },
                    contentPadding = innerPadding
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawSaveBookmarks,
                text = stringResource(id = R.string.readlater_label_nothing_to_read_later),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            )
        } else {
            val (paddings, margins) = calculatePaddingMargins(contentPadding)
            FileLazyVerticalGrid(
                uiState = FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List),
                lazyPagingItems = lazyPagingItems,
                contentPadding = paddings,
                onItemClick = onFileClick,
                onItemInfoClick = onFileInfoClick,
                state = lazyGridState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(margins)
            )
        }
    }
}
