package com.sorrowblue.comicviewer.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowEmptyData

@Preview
@Composable
private fun FolderScreenPreview() {
    val pagingDataFlow = PagingData.flowData<File> {
        fakeBookFile(it)
    }
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
    PreviewTheme {
        FolderScreen(
            navigator = rememberCanonicalScaffoldNavigator(),
            uiState = FolderScreenUiState(folderAppBarUiState = FolderAppBarUiState("Preview title")),
            lazyPagingItems = lazyPagingItems,
            onFileInfoSheetAction = {},
            onFolderTopAppBarAction = {},
            onFolderContentsAction = {},
        )
    }
}

@Preview
@Composable
private fun FolderScreenEmptyPreview() {
    val pagingDataFlow = PagingData.flowEmptyData<File>()
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
    PreviewTheme {
        FolderScreen(
            navigator = rememberCanonicalScaffoldNavigator(),
            uiState = FolderScreenUiState(folderAppBarUiState = FolderAppBarUiState("Preview title")),
            lazyPagingItems = lazyPagingItems,
            onFileInfoSheetAction = {},
            onFolderTopAppBarAction = {},
            onFolderContentsAction = {},
        )
    }
}
