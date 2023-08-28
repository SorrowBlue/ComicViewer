package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Dns
import androidx.compose.material.icons.twotone.SdStorage
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookshelfFolderRow(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(onClick = {}) {
        Column(
            Modifier
                .fillMaxSize()
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(),
                    onClick = onClick,
                    onLongClick = onLongClick
                )) {
            Box {
                AsyncImage(
                    model = bookshelfFolder.folder,
                    contentScale = ContentScale.Crop,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                        )
                        .clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp))
                )

                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    PlainTooltipBox(
                        tooltip = { Text("Settings") }
                    ) {
                        IconButton(
                            modifier = Modifier
                                .tooltipAnchor()
                                .align(Alignment.TopEnd),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(),
                            onClick = { }) {
                            Icon(
                                if (bookshelfFolder.bookshelf is SmbServer) Icons.TwoTone.Dns else Icons.TwoTone.SdStorage,
                                contentDescription = "お気に入り"
                            )
                        }
                    }
                }

            }

            Text(
                text = stringResource(
                    id = R.string.bookshelf_list_label_files,
                    bookshelfFolder.bookshelf.fileCount
                ),
                style = Typography().labelSmall,
                minLines = 2,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Text(
                text = BookshelfConverter.displayName(
                    bookshelfFolder.bookshelf,
                    bookshelfFolder.folder
                ),
                style = Typography().labelMedium,
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun ArticleRowPreview() {
    AppMaterialTheme {
        Surface {
            BookshelfFolderRow(
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
