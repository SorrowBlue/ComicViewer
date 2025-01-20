package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBarUiState
import com.sorrowblue.comicviewer.favorite.section.FavoriteTopAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.favorite.generated.resources.Res
import comicviewer.feature.favorite.generated.resources.favorite_label_no_favorites
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

interface FavoriteScreenNavigator {
    fun navigateUp()
    fun onEditClick(favoriteId: FavoriteId)
    fun onSettingsClick()
    fun onFileClick(file: File, favoriteId: FavoriteId)
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun onOpenFolderClick(file: File)
}

@Serializable
data class Favorite(val favoriteId: FavoriteId)

@Destination<Favorite>
@Composable
internal fun FavoriteScreen(
    route: Favorite,
    navigator: FavoriteScreenNavigator,
    state: FavoriteScreenState = rememberFavoriteScreenState(route = route),
) {
    val uiState = state.uiState
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteScreen(
        navigator = state.navigator,
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onFavoriteTopAppBarAction = state::onFavoriteTopAppBarAction,
        onFavoriteContentsAction = state::onFavoriteContentsAction,
        lazyGridState = state.lazyGridState,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
    )

    val currentNavigator by rememberUpdatedState(navigator)
    EventEffect(state.events) {
        when (it) {
            FavoriteScreenEvent.Back -> currentNavigator.navigateUp()
            is FavoriteScreenEvent.Favorite ->
                currentNavigator.onFavoriteClick(it.bookshelfId, it.path)

            is FavoriteScreenEvent.File -> currentNavigator.onFileClick(it.file, state.favoriteId)
            is FavoriteScreenEvent.OpenFolder ->
                currentNavigator.onOpenFolderClick(it.file)

            FavoriteScreenEvent.Settings -> currentNavigator.onSettingsClick()
            is FavoriteScreenEvent.Edit -> currentNavigator.onEditClick(it.favoriteId)
        }
    }

    // TODO NavTabHandler(onClick = state::onNavClick)
}

internal data class FavoriteScreenUiState(
    val favoriteAppBarUiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
)

@Composable
private fun FavoriteScreen(
    uiState: FavoriteScreenUiState,
    navigator: ThreePaneScaffoldNavigator<File.Key>,
    lazyPagingItems: LazyPagingItems<File>,
    onFavoriteTopAppBarAction: (FavoriteTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetNavigator) -> Unit,
    onFavoriteContentsAction: (FavoriteContentsAction) -> Unit,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        topBar = {
            FavoriteTopAppBar(
                uiState = uiState.favoriteAppBarUiState,
                onAction = onFavoriteTopAppBarAction,
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        navigator = navigator,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        FavoriteContents(
            fileLazyVerticalGridUiState = uiState.fileLazyVerticalGridUiState,
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            contentPadding = contentPadding,
            onAction = onFavoriteContentsAction,
        )
    }
}

internal sealed interface FavoriteContentsAction {

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FavoriteContentsAction

    data class FileInfo(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FavoriteContentsAction
}

@Composable
private fun FavoriteContents(
    fileLazyVerticalGridUiState: FileLazyVerticalGridUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onAction: (FavoriteContentsAction) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawResumeFolder,
            text = stringResource(Res.string.favorite_label_no_favorites),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        FileLazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            uiState = fileLazyVerticalGridUiState,
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onItemClick = { onAction(FavoriteContentsAction.File(it)) },
            onItemInfoClick = { onAction(FavoriteContentsAction.FileInfo(it)) },
            state = lazyGridState
        )
    }
}
