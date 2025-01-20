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
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBar
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawSaveBookmarks
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.readlater.generated.resources.Res
import comicviewer.feature.readlater.generated.resources.readlater_label_nothing_to_read_later
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

interface ReadLaterScreenNavigator {
    fun onSettingsClick()
    fun onFileClick(file: File)
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun onOpenFolderClick(file: File)
}

@Serializable
data object ReadLater

@Destination<ReadLater>
@Composable
internal fun ReadLaterScreen(
    navigator: ReadLaterScreenNavigator,
    state: ReadLaterScreenState = rememberReadLaterScreenState(),
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
    EventEffect(state.events) {
        when (it) {
            is ReadLaterScreenEvent.Favorite ->
                currentNavigator.onFavoriteClick(it.bookshelfId, it.path)

            is ReadLaterScreenEvent.File -> currentNavigator.onFileClick(it.file)
            is ReadLaterScreenEvent.OpenFolder -> currentNavigator.onOpenFolderClick(it.file)
            ReadLaterScreenEvent.Settings -> currentNavigator.onSettingsClick()
        }
    }
//    TODO NavTabHandler(onClick = state::onNavClick)
}

@Composable
private fun ReadLaterScreen(
    lazyPagingItems: LazyPagingItems<File>,
    navigator: ThreePaneScaffoldNavigator<File.Key>,
    lazyGridState: LazyGridState,
    onTopAppBarAction: (ReadLaterTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetNavigator) -> Unit,
    onContentsAction: (ReadLaterContentsAction) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            ReadLaterTopAppBar(
                onAction = onTopAppBarAction,
                scrollBehavior = scrollBehavior,
                scrollableState = lazyGridState
            )
        },
        extraPane = { contentKey ->
            FileInfoSheet(
                fileKey = contentKey,
                onAction = onFileInfoSheetAction,
                isOpenFolderEnabled = true
            )
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
            text = stringResource(Res.string.readlater_label_nothing_to_read_later),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        FileLazyVerticalGrid(
            uiState = FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List),
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onItemClick = { onAction(ReadLaterContentsAction.File(it)) },
            onItemInfoClick = { onAction(ReadLaterContentsAction.FileInfo(it)) },
            state = lazyGridState,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
