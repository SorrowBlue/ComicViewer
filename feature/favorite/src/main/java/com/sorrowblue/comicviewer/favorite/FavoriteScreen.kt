package com.sorrowblue.comicviewer.favorite

import android.os.Parcelable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBarUiState
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlinx.parcelize.Parcelize

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FavoriteScreen(
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File, FavoriteId) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    state: FavoriteScreenState = rememberFavoriteScreenState()
) {
    val uiState = state.uiState
    val navigator = state.navigator
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    FavoriteScreen(
        navigator = navigator,
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = onBackClick,
        onEditClick = { onEditClick(state.favoriteId) },
        onFileListTypeChange = state::toggleFileListType,
        onGridSizeChange = state::toggleGridSize,
        onDeleteClick = { state.delete(onBackClick) },
        onSettingsClick = onSettingsClick,
        onFileClick = { onClickFile(it, state.favoriteId) },
        onFileInfoClick = state::onFileInfoClick,
        lazyGridState = lazyGridState,
        contentPadding = contentPadding,
        onFavoriteClick = onFavoriteClick,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onOpenFolderClick = onOpenFolderClick,
        onReadLaterClick = state::onReadLaterClick,
    )

    NavTabHandler(onClick = state::onNavClick)
}

@Parcelize
internal data class FavoriteScreenUiState(
    val file: File? = null,
    val favoriteAppBarUiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    val fileContentType: FileContentType = FileContentType.List,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun FavoriteScreen(
    uiState: FavoriteScreenUiState,
    navigator: ThreePaneScaffoldNavigator,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFileListTypeChange: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onReadLaterClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onGridSizeChange: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    lazyGridState: LazyGridState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        topBar = {
            FavoriteAppBar(
                uiState = uiState.favoriteAppBarUiState,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onFileListTypeChange = onFileListTypeChange,
                onGridSizeChange = onGridSizeChange,
                onDeleteClick = onDeleteClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior
            )
        },
        extraPane = { innerPadding ->
            val file = uiState.file
            if (file != null) {
                FileInfoSheet(
                    file = file,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = { onReadLaterClick(file) },
                    onFavoriteClick = { onFavoriteClick(file) },
                    onOpenFolderClick = { onOpenFolderClick(file) },
                    contentPadding = innerPadding
                )
            }
        },
        navigator = navigator,
        contentPadding = contentPadding,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawResumeFolder,
                text = stringResource(id = R.string.favorite_label_no_favorites),
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
    }
}
