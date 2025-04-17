package com.sorrowblue.comicviewer.feature.collection.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.collection.component.CollectionListCardItem
import com.sorrowblue.comicviewer.feature.collection.component.CollectionListItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import com.sorrowblue.comicviewer.framework.ui.layout.ResponsiveLazyColumn
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.itemKey
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_delete
import comicviewer.feature.collection.generated.resources.collection_label_edit
import comicviewer.feature.collection.generated.resources.collection_label_no_collection
import org.jetbrains.compose.resources.stringResource

internal sealed interface CollectionListContentsAction {
    data class CollectionListClick(val id: CollectionId) : CollectionListContentsAction
    data class EditClick(val collection: Collection) : CollectionListContentsAction
    data class DeleteClick(val id: CollectionId) : CollectionListContentsAction
}

@Composable
internal fun CollectionListContents(
    lazyPagingItems: LazyPagingItems<Collection>,
    onAction: (CollectionListContentsAction) -> Unit,
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
        ResponsiveLazyColumn(
            state = lazyListState,
            contentPadding = contentPadding + PaddingValues(bottom = 88.dp),
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
                            var expanded by remember { mutableStateOf(false) }
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(ComicIcons.MoreVert, null)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.collection_label_edit)) },
                                    onClick = {
                                        expanded = false
                                        onAction(CollectionListContentsAction.EditClick(it))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(Res.string.collection_label_delete)) },
                                    onClick = {
                                        expanded = false
                                        onAction(CollectionListContentsAction.DeleteClick(it.id))
                                    }
                                )
                            }
                        }
                    }
                    if (isCompact) {
                        CollectionListItem(
                            collection = it,
                            onClick = { onAction(CollectionListContentsAction.CollectionListClick(it.id)) },
                            content = content
                        )
                    } else {
                        CollectionListCardItem(
                            collection = it,
                            onClick = { onAction(CollectionListContentsAction.CollectionListClick(it.id)) },
                            content = content
                        )
                    }
                }
            }
        }
    }
}
