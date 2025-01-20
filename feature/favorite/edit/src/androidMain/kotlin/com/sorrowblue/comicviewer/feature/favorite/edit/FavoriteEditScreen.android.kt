package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@Preview
@Composable
private fun FavoriteEditScreenPreview() {
    val lazyPagingItems = PagingData.flowData<File> { fakeBookFile(it) }.collectAsLazyPagingItems()
    ComicTheme {
        FavoriteEditScreen(
            uiState = FavoriteEditScreenUiState(),
            lazyPagingItems = lazyPagingItems,
            onBackClick = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}
