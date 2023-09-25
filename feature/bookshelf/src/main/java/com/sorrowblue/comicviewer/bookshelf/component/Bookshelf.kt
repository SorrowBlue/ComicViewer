package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.debugPlaceholder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Bookshelf(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(onClick = {}, modifier = modifier) {
        ListItem(
            leadingContent = {
                AsyncImage(
                    model = bookshelfFolder.folder,
                    contentScale = ContentScale.Crop,
                    placeholder = debugPlaceholder(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(128.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                )
            },
            headlineContent = {
                Text(
                    text = BookshelfConverter.displayName(
                        bookshelfFolder.bookshelf,
                        bookshelfFolder.folder
                    )
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(
                        id = R.string.bookshelf_list_label_files,
                        bookshelfFolder.bookshelf.fileCount
                    )
                )
            },
            trailingContent = {
                Icon(
                    if (bookshelfFolder.bookshelf is SmbServer) ComicIcons.Dns else ComicIcons.SdStorage,
                    contentDescription = null
                )
            },
            modifier = Modifier
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = onClick,
                    onLongClick = onLongClick
                )
        )
    }
}

@Preview
@Composable
private fun PreviewArticleRow() {
    ComicTheme {
        Surface {
            Bookshelf(
                bookshelfFolder = BookshelfFolder(
                    InternalStorage(BookshelfId(0), "aaaaaaaaaaaaaaaaa", 0) to Folder(
                        BookshelfId(0),
                        "",
                        "",
                        "",
                        0,
                        0
                    )
                ),
                {},
                {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
