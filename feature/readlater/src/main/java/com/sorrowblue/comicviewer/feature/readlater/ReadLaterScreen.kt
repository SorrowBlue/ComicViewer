package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
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
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBar
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawSaveBookmarks
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.calculatePaddingMargins
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
        navigator = navigator,
        state = rememberReadLaterScreenState(),
    )
}

@Composable
private fun ReadLaterScreen(
    navigator: ReadLaterScreenNavigator,
    state: ReadLaterScreenState,
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    ReadLaterScreen(
        lazyPagingItems = lazyPagingItems,
        navigator = state.navigator,
        lazyGridState = state.lazyGridState,
        onTopAppBarAction = state::onTopAppBarAction,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
        onContentsAction = state::onContentsAction
    )

    val currentNavigator by rememberUpdatedState(navigator)
    LaunchedEventEffect(state.event) {
        when (it) {
            is ReadLaterScreenEvent.Favorite -> currentNavigator.onFavoriteClick(it.file)
            is ReadLaterScreenEvent.File -> currentNavigator.onFileClick(it.file)
            ReadLaterScreenEvent.Settings -> currentNavigator.onSettingsClick()
        }
    }
    NavTabHandler(onClick = state::onNavClick)
}

@Composable
private fun ReadLaterScreen(
    lazyPagingItems: LazyPagingItems<File>,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    lazyGridState: LazyGridState,
    onTopAppBarAction: (ReadLaterTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetAction) -> Unit,
    onContentsAction: (ReadLaterContentsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            ReadLaterTopAppBar(
                onAction = onTopAppBarAction,
                scrollBehavior = scrollBehavior
            )
        },
        extraPane = { content ->
            FileInfoSheet(uiState = content, onAction = onFileInfoSheetAction)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        ReadLaterContents(
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            onAction = onContentsAction,
            contentPadding = contentPadding
        )
    }
}

internal sealed interface ReadLaterContentsAction {

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterContentsAction

    data class FileInfo(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterContentsAction
}

@Composable
private fun ReadLaterContents(
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onAction: (ReadLaterContentsAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
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
            onItemClick = { onAction(ReadLaterContentsAction.File(it)) },
            onItemInfoClick = { onAction(ReadLaterContentsAction.FileInfo(it)) },
            state = lazyGridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(margins)
        )
    }
}
