package com.sorrowblue.comicviewer.folder.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderContents(
    contentPadding: PaddingValues,
    pagingItems: LazyPagingItems<File>,
    itemClick: (File) -> Unit,
    itemLongClick: (File) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyGridState = rememberLazyGridState(),
    nestedScrollConnection: NestedScrollConnection = rememberNestedScrollInteropConnection()
) {
    if (pagingItems.loadState.refresh is LoadState.Loading) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = contentPadding.calculateTopPadding())
        )
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = lazyListState,
        contentPadding = PaddingValues(
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current) + AppMaterialTheme.dimens.margin,
            top = contentPadding.calculateTopPadding() + AppMaterialTheme.dimens.margin,
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current) + AppMaterialTheme.dimens.margin,
            bottom = contentPadding.calculateBottomPadding() + AppMaterialTheme.dimens.margin
        ),
        verticalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer),
        horizontalArrangement = Arrangement.spacedBy(AppMaterialTheme.dimens.spacer),
        modifier = modifier.nestedScroll(nestedScrollConnection)
    ) {
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.path },
            contentType = { pagingItems.itemContentType { "contentType" } }
        ) {
            val item = pagingItems[it]
            if (item != null) {
                FileItem(
                    file = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(onLongClick = { itemLongClick(item) },
                            onClick = { itemClick(item) })
                )
            }
        }
    }
}
