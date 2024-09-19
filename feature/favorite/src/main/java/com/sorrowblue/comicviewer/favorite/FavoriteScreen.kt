package com.sorrowblue.comicviewer.favorite

import android.os.Parcelable
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
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphTransitions
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBarUiState
import com.sorrowblue.comicviewer.favorite.section.FavoriteTopAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteTopAppBarAction
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.adaptive.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.calculatePaddingMargins
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlinx.parcelize.Parcelize

interface FavoriteScreenNavigator {
    fun navigateUp()
    fun onEditClick(favoriteId: FavoriteId)
    fun onSettingsClick()
    fun onFileClick(file: File, favoriteId: FavoriteId)
    fun onFavoriteClick(file: File)
    fun onOpenFolderClick(file: File)
}

class FavoriteArgs(val favoriteId: FavoriteId)

@Destination<FavoriteGraph>(
    navArgs = FavoriteArgs::class,
    style = FavoriteGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FavoriteScreen(args: FavoriteArgs, navigator: FavoriteScreenNavigator) {
    FavoriteScreen(navigator = navigator, state = rememberFavoriteScreenState(args = args))
}

@Composable
private fun FavoriteScreen(
    navigator: FavoriteScreenNavigator,
    state: FavoriteScreenState,
) {
    val uiState = state.uiState
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteScreen(
        navigator = state.navigator,
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onFavoriteTopAppBarAction = state::onFavoriteTopAppBarAction,
        snackbarHostState = state.snackbarHostState,
        onFavoriteContentsAction = state::onFavoriteContentsAction,
        lazyGridState = state.lazyGridState,
        onFileInfoSheetAction = state::onFileInfoSheetAction,
    )

    val currentNavigator by rememberUpdatedState(navigator)
    LaunchedEventEffect(state.event) {
        when (it) {
            FavoriteScreenEvent.Back -> currentNavigator.navigateUp()
            is FavoriteScreenEvent.Favorite -> currentNavigator.onFavoriteClick(it.file)
            is FavoriteScreenEvent.File -> currentNavigator.onFileClick(it.file, state.favoriteId)
            is FavoriteScreenEvent.OpenFolder -> currentNavigator.onOpenFolderClick(it.file)
            FavoriteScreenEvent.Settings -> currentNavigator.onSettingsClick()
            is FavoriteScreenEvent.Edit -> currentNavigator.onEditClick(it.favoriteId)
        }
    }

    NavTabHandler(onClick = state::onNavClick)
}

@Parcelize
internal data class FavoriteScreenUiState(
    val favoriteAppBarUiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
) : Parcelable

@Composable
private fun FavoriteScreen(
    uiState: FavoriteScreenUiState,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    lazyPagingItems: LazyPagingItems<File>,
    onFavoriteTopAppBarAction: (FavoriteTopAppBarAction) -> Unit,
    onFileInfoSheetAction: (FileInfoSheetAction) -> Unit,
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
                scrollBehavior = scrollBehavior
            )
        },
        extraPane = { contentPadding, fileInfoUiState ->
            FileInfoSheet(
                uiState = fileInfoUiState,
                onAction = onFileInfoSheetAction,
                contentPadding = contentPadding,
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
            text = stringResource(id = R.string.favorite_label_no_favorites),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
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
            onItemClick = { onAction(FavoriteContentsAction.File(it)) },
            onItemInfoClick = { onAction(FavoriteContentsAction.FileInfo(it)) },
            state = lazyGridState
        )
    }
}
