package com.sorrowblue.comicviewer.feature.collection.editor.basic.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_no_books
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BasicCollectionContent(
    lazyPagingItems: LazyPagingItems<File>,
    onDeleteClick: (File) -> Unit,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    if (lazyPagingItems.isEmptyData) {
        EmptyContent(
            imageVector = ComicIcons.UndrawNoData,
            text = stringResource(Res.string.collection_editor_label_no_books),
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        )
    } else {
        LazyColumn(
            state = state,
            contentPadding = contentPadding,
            modifier = Modifier
        ) {
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { "${it.bookshelfId.value}${it.path}" }
            ) {
                val item = lazyPagingItems[it]
                if (item != null) {
                    ListItem(
                        headlineContent = { Text(item.name) },
                        leadingContent = {
                            AsyncImage(
                                model = FileThumbnail.from(item),
                                null,
                                Modifier.size(56.dp)
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { onDeleteClick(item) }) {
                                Icon(ComicIcons.Delete, null)
                            }
                        }
                    )
                }
            }
        }
    }
}
