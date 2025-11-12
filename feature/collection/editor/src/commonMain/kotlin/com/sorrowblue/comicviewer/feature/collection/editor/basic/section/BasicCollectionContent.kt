package com.sorrowblue.comicviewer.feature.collection.editor.basic.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_label_no_books
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BasicCollectionContent(
    lazyPagingItems: LazyPagingItems<File>,
    onDeleteClick: (File) -> Unit,
    header: @Composable () -> Unit,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(),
) {
    val currentHeader = remember { movableContentOf { header() } }
    if (lazyPagingItems.isEmptyData) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            currentHeader()
            Image(
                imageVector = ComicIcons.UndrawNoData,
                contentDescription = null,
                modifier = Modifier
                    .sizeIn(maxWidth = 120.dp, maxHeight = 120.dp)
                    .padding(top = ComicTheme.dimension.padding),
            )
            Text(
                text = stringResource(Res.string.collection_editor_label_no_books),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = ComicTheme.dimension.padding),
            )
        }
    } else {
        LazyColumn(
            state = state,
            contentPadding = contentPadding,
            modifier = Modifier,
        ) {
            item { currentHeader() }
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { "${it.bookshelfId.value}${it.path}" },
            ) {
                val item = lazyPagingItems[it]
                if (item != null) {
                    ListItem(
                        headlineContent = { Text(item.name) },
                        leadingContent = {
                            AsyncImage(
                                model = FileThumbnail.from(item),
                                null,
                                Modifier.size(56.dp),
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { onDeleteClick(item) }) {
                                Icon(ComicIcons.Delete, null)
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}
