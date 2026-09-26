/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavKey
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBar
import com.sorrowblue.comicviewer.feature.collection.section.CollectionAppBarUiState
import com.sorrowblue.comicviewer.feature.collection.section.CollectionContents
import com.sorrowblue.comicviewer.feature.file.nav.FileInfoNavKey
import com.sorrowblue.comicviewer.feature.folder.nav.FolderNavKey
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@NavEdge(to = FileInfoNavKey::class)
@NavEdge(to = FolderNavKey::class)
@NavDestination(CollectionNavKey::class)
@Composable
internal fun CollectionScreen(
    uiState: CollectionScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFileListDisplayClick: () -> Unit,
    onGridSizeClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CollectionAppBar(
                uiState = uiState.appBarUiState,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onSettingsClick = onSettingsClick,
                onFileListDisplayClick = onFileListDisplayClick,
                onGridSizeClick = onGridSizeClick,
            )
        },
        modifier = modifier,
    ) { contentPadding ->
        CollectionContents(
            fileLazyVerticalGridUiState = uiState.fileLazyVerticalGridUiState,
            lazyPagingItems = lazyPagingItems,
            onItemClick = onFileClick,
            onItemInfoClick = onFileInfoClick,
            contentPadding = contentPadding + PaddingValues(16.dp),
        )
    }
}

@NavPreview(CollectionNavKey::class, primary = true)
@Preview
@Composable
private fun CollectionScreenPreview() {
    PreviewTheme {
        CollectionScreen(
            uiState = remember {
                CollectionScreenUiState(
                    appBarUiState = CollectionAppBarUiState(title = "Collection Preview"),
                )
            },
            lazyPagingItems = PagingData.flowData<File> { fakeBookFile(it) }
                .collectAsLazyPagingItems(),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onSettingsClick = {},
            onFileListDisplayClick = {},
            onGridSizeClick = {},
            onFileClick = {},
            onFileInfoClick = {},
        )
    }
}
