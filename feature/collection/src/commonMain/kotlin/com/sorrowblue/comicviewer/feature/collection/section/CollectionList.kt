package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.component.CollectionActionsDropdown
import com.sorrowblue.comicviewer.feature.collection.component.CollectionListCardItem
import com.sorrowblue.comicviewer.feature.collection.component.CollectionListItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_no_collection
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CollectionList(
    lazyPagingItems: LazyPagingItems<Collection>,
    onItemClick: (Collection) -> Unit,
    onEditClick: (Collection) -> Unit,
    onDeleteClick: (Collection) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawNoData,
            text = stringResource(Res.string.collection_label_no_collection),
            modifier = modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        val isCompact = isCompactWindowClass()
        LazyColumn(
            state = lazyListState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(
                if (isCompact) 0.dp else ComicTheme.dimension.padding,
                Alignment.Top
            ),
            modifier = modifier
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id.value },
                contentType = { isCompact }
            ) { index ->
                lazyPagingItems[index]?.let {
                    val content = remember {
                        movableContentOf {
                            CollectionActionsDropdown(
                                onEditClick = { onEditClick(it) },
                                onDeleteClick = { onDeleteClick(it) }
                            )
                        }
                    }
                    if (isCompact) {
                        CollectionListItem(
                            collection = it,
                            onClick = { onItemClick(it) },
                            content = content
                        )
                    } else {
                        CollectionListCardItem(
                            collection = it,
                            onClick = { onItemClick(it) },
                            content = content
                        )
                    }
                }
            }
        }
    }
}
