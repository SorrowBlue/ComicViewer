package com.sorrowblue.comicviewer.file.component

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@PreviewMultiScreen
@Composable
private fun GridFileLazyGridPreview() {
    val lazyPagingItems = PagingData.flowData<File> { fakeBookFile(it) }.collectAsLazyPagingItems()
    PreviewTheme {
        Scaffold {
            FileLazyVerticalGrid(
                uiState = FileLazyVerticalGridUiState(),
                lazyPagingItems = lazyPagingItems,
                onItemClick = {},
                onItemInfoClick = {},
                contentPadding = it
            )
        }
    }
}
