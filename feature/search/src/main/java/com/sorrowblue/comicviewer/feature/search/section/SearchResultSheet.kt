package com.sorrowblue.comicviewer.feature.search.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.feature.search.R
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFileSearching
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

@Composable
internal fun SearchResultSheet(
    query: String,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues = PaddingValues(),
    lazyListState: LazyGridState = rememberLazyGridState(),
    onFileClick: (File) -> Unit = {},
    onFileLongClick: (File) -> Unit = {},
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContents(
            query = query,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding()
        )
    } else {
        FileLazyVerticalGrid(
            uiState = FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List),
            state = lazyListState,
            contentPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            onItemClick = onFileClick,
            onItemInfoClick = onFileLongClick
        )
    }
}

@Composable
private fun EmptyContents(
    query: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ComicIcons.UndrawFileSearching,
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = stringResource(id = R.string.search_label_not_found, query),
            modifier = Modifier.width(200.dp)
        )
    }
}
