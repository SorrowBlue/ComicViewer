package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.layout.PreviewCompliantNavigation

@PreviewMultiScreen
@Composable
private fun SearchScreenPreview() {
    PreviewCompliantNavigation {
        val pagingDataFlow = PagingData.flowData<File> { fakeBookFile(it) }
        val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
        SearchScreen(
            uiState = SearchScreenUiState(),
            lazyPagingItems = lazyPagingItems,
            navigator = rememberCanonicalScaffoldNavigator(),
            lazyGridState = rememberLazyGridState(),
            onSearchTopAppBarAction = {},
            onFileInfoSheetAction = {},
            onSearchContentsAction = {}
        )
    }
}
