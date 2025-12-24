package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBar
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarUiState
import com.sorrowblue.comicviewer.feature.collection.section.CollectionContents
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState

internal data class CollectionScreenUiState(
    val appBarUiState: CollectionAppBarUiState = CollectionAppBarUiState(),
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
)

@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.CollectionScreen(
    uiState: CollectionScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveNavigationSuiteScaffold(modifier = modifier) {
        Scaffold(
            topBar = {
                CollectionAppBar(
                    uiState = uiState.appBarUiState,
                    onBackClick = onBackClick,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick,
                    onSettingsClick = onSettingsClick,
                )
            },
        ) { contentPadding ->
            CollectionContents(
                fileLazyVerticalGridUiState = uiState.fileLazyVerticalGridUiState,
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                onItemClick = onFileClick,
                onItemInfoClick = onFileInfoClick,
                contentPadding = contentPadding,
            )
        }
    }
}
