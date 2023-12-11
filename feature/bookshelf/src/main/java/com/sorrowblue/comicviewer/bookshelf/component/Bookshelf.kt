package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.PreviewMobile
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Text
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Bookshelf(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = onClick,
        )
    ) {
        Box {
            AsyncImage(
                model = bookshelfFolder.folder,
                contentScale = ContentScale.Crop,
                placeholder = rememberDebugPlaceholder(),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 2f)
                    .background(
                        ComicTheme.colorScheme.surfaceContainerHighest,
                        shape = CardDefaults.shape
                    )
                    .clip(CardDefaults.shape)
            )
            FilledTonalIconButton(
                onClick = { /*TODO*/ },
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    if (bookshelfFolder.bookshelf is SmbServer) ComicIcons.Dns else ComicIcons.SdStorage,
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row {
            Text(
                text = BookshelfConverter.displayName(
                    bookshelfFolder.bookshelf,
                    bookshelfFolder.folder
                ),
                style = ComicTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically),
            )
            IconButton(
                onClick = onInfoClick,
                modifier = Modifier
                    .align(Alignment.Top)
            ) {
                Icon(
                    imageVector = ComicIcons.MoreVert,
                    contentDescription = null
                )
            }
        }
        Text(
            text = pluralStringResource(
                id = R.plurals.bookshelf_list_label_files,
                bookshelfFolder.bookshelf.fileCount,
                bookshelfFolder.bookshelf.fileCount,
            ),
            style = ComicTheme.typography.labelMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
    }
}

@PreviewMobile
@Composable
private fun PreviewArticleRow() {
    PreviewTheme {
        Bookshelf(
            bookshelfFolder = BookshelfFolder(
                InternalStorage(BookshelfId(0), "display name", 0),
                Folder(
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
