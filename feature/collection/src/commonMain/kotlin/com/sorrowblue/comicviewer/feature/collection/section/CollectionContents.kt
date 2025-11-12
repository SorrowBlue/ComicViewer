package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File as FileModel
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_no_contents
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CollectionContents(
    fileLazyVerticalGridUiState: FileLazyVerticalGridUiState,
    lazyPagingItems: LazyPagingItems<FileModel>,
    lazyGridState: LazyGridState,
    onItemClick: (FileModel) -> Unit,
    onItemInfoClick: (FileModel) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawResumeFolder,
            text = stringResource(Res.string.collection_label_no_contents),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        )
    } else {
        FileLazyVerticalGrid(
            modifier = Modifier
                .fillMaxSize(),
            uiState = fileLazyVerticalGridUiState,
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            onItemClick = onItemClick,
            onItemInfoClick = onItemInfoClick,
            state = lazyGridState,
        )
    }
}
