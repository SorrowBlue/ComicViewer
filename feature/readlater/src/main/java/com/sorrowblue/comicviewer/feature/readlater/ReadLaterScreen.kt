package com.sorrowblue.comicviewer.feature.readlater

import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterAppBar
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawSaveBookmarks
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlinx.parcelize.Parcelize

interface ReadLaterScreenNavigator {
    fun onSettingsClick()
    fun onFileClick(file: File)
    fun onFavoriteClick(file: File)
    fun onOpenFolderClick(file: File)
}

@Destination
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
    val uiState = state.uiState
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    ReadLaterScreen(
        uiState = uiState,
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
}

@Parcelize
internal data class ReadLaterScreenUiState(
    val file: File? = null,
) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ReadLaterScreen(
    uiState: ReadLaterScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    navigator: ThreePaneScaffoldNavigator,
    lazyGridState: LazyGridState,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onClearAllClick: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onReadLaterClick: (File) -> Unit,
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
            if (uiState.file != null) {
                FileInfoSheet(
                    file = uiState.file,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = { onReadLaterClick(uiState.file) },
                    onFavoriteClick = { onFavoriteClick(uiState.file) },
                    onOpenFolderClick = { onOpenFolderClick(uiState.file) },
                    contentPadding = innerPadding
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawSaveBookmarks,
                text = stringResource(id = R.string.readlater_label_nothing_to_read_later),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            FileContent(
                type = FileContentType.List,
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding,
                onFileClick = onFileClick,
                onInfoClick = onFileInfoClick,
                state = lazyGridState
            )
        }
    }
}
