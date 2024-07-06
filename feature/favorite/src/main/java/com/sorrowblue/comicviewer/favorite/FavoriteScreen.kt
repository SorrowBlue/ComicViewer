package com.sorrowblue.comicviewer.favorite

import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphTransitions
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBarUiState
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.rememberFileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
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
    FavoriteScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onEditClick = navigator::onEditClick,
        onSettingsClick = navigator::onSettingsClick,
        onFileClick = navigator::onFileClick,
        onFavoriteClick = navigator::onFavoriteClick,
        onOpenFolderClick = navigator::onOpenFolderClick
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun FavoriteScreen(
    args: FavoriteArgs,
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File, FavoriteId) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    state: FavoriteScreenState = rememberFavoriteScreenState(args = args),
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
        onFileListChange = state::toggleFileListType,
        onGridSizeChange = state::toggleGridSize,
        onDeleteClick = { state.delete(onBackClick) },
        onSettingsClick = onSettingsClick,
        onFileClick = { onFileClick(it, state.favoriteId) },
        onFileInfoClick = state::onFileInfoClick,
        lazyGridState = lazyGridState,
        onFavoriteClick = onFavoriteClick,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onOpenFolderClick = onOpenFolderClick,
        onReadLaterClick = state::onReadLaterClick,
    )

    NavTabHandler(onClick = state::onNavClick)
}

@Parcelize
internal data class FavoriteScreenUiState(
    val favoriteAppBarUiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    val display: FolderDisplaySettings.Display = FolderDisplaySettings.Display.List,
    val columnSize: FolderDisplaySettings.ColumnSize = FolderDisplaySettings.ColumnSize.Medium,
    val isThumbnailEnabled: Boolean = true,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun FavoriteScreen(
    uiState: FavoriteScreenUiState,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFileListChange: () -> Unit,
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
                onFileListChange = onFileListChange,
                onGridSizeChange = onGridSizeChange,
                onDeleteClick = onDeleteClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior
            )
        },
        extraPane = { innerPadding ->
            val fileInfo by remember(navigator.currentDestination?.content) {
                mutableStateOf(navigator.currentDestination?.content)
            }
            fileInfo?.let {
                FileInfoSheet(
                    fileInfoUiState = it,
                    scaffoldDirective = navigator.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = { onReadLaterClick(it.file) },
                    onFavoriteClick = { onFavoriteClick(it.file) },
                    onOpenFolderClick = { onOpenFolderClick(it.file) },
                    contentPadding = innerPadding
                )
            }
        },
        navigator = navigator,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawResumeFolder,
                text = stringResource(id = R.string.favorite_label_no_favorites),
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
    }
}
