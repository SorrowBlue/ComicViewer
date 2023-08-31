package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBarUiState
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentUiState
import com.sorrowblue.comicviewer.framework.compose.isEmptyData
import com.sorrowblue.comicviewer.framework.resource.R as FrameworkResourceR

@Composable
internal fun FavoriteRoute(
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File) -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = onBackClick,
        onEditClick = { onEditClick(viewModel.favoriteId) },
        onFileListTypeChange = viewModel::toggleFileListType,
        onGridSizeChange = viewModel::toggleGridSize,
        onDeleteClick = { viewModel.delete(onBackClick) },
        onSettingsClick = onSettingsClick,
        onClickFile = onClickFile,
        onClickLongFile = viewModel::showFileInfoSheet,
    )
}

internal data class FavoriteScreenUiState(
    val favoriteAppBarUiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    val fileContentUiState: FileContentUiState = FileContentUiState()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteScreen(
    uiState: FavoriteScreenUiState = FavoriteScreenUiState(),
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onFileListTypeChange: () -> Unit = {},
    onGridSizeChange: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onClickFile: (File) -> Unit = {},
    onClickLongFile: (File) -> Unit = {},
    lazyStaggeredGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
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
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(FrameworkResourceR.drawable.ic_undraw_resume_folder_re_e0bi),
                    ""
                )
                Text(stringResource(id = R.string.favorite_label_no_favorites))
            }
        } else {
            FileContent(
                uiState = uiState.fileContentUiState,
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                onClickItem = onClickFile,
                onLongClickItem = onClickLongFile,
                state = lazyStaggeredGridState
            )
        }
    }
}
