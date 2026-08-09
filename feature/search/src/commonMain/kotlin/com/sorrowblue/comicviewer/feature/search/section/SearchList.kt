/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search.section

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawFileSearching
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.search.generated.resources.Res
import comicviewer.feature.search.generated.resources.search_label_not_found
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SearchList(
    searchCondition: SearchCondition,
    lazyPagingItems: LazyPagingItems<File>,
    lazyListState: LazyGridState,
    onItemClick: (File) -> Unit,
    onItemInfoClick: (File) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawFileSearching,
            text = stringResource(Res.string.search_label_not_found, searchCondition.query),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding(),
        )
    } else {
        @OptIn(ExperimentalFoundationApi::class)
        FileLazyVerticalGrid(
            uiState = FileLazyVerticalGridUiState(fileListDisplay = FileListDisplay.List),
            state = lazyListState,
            contentPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            onItemClick = onItemClick,
            onItemInfoClick = onItemInfoClick,
        )
    }
}
