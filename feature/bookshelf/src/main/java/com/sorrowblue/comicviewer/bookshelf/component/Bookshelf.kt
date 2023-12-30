package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sorrowblue.comicviewer.bookshelf.BookshelfConverter
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.symbols.DocumentUnknown
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AsyncImage2
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun Bookshelf(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(),
            onClick = onClick,
        )
    ) {
        BoxWithConstraints {
            if (300.dp < maxWidth) {
                Row(modifier = Modifier.height(160.dp)) {
                    AsyncImage2(
                        model = bookshelfFolder.folder,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        loading = {
                            Icon(
                                imageVector = ComicIcons.DocumentUnknown,
                                contentDescription = null
                            )
                        },
                        error = {
                            Icon(imageVector = ComicIcons.Image, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .background(
                                ComicTheme.colorScheme.surfaceVariant,
                                shape = CardDefaults.shape
                            )
                            .clip(CardDefaults.shape),
                    )
                    Column {
                        Row {
                            Text(
                                text = BookshelfConverter.displayName(
                                    bookshelfFolder.bookshelf,
                                    bookshelfFolder.folder
                                ),
                                style = ComicTheme.typography.titleMedium.copy(
                                    letterSpacing = 0.sp
                                ),
                                maxLines = 2,
                                minLines = 2,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .weight(1f),
                            )
                            IconButton(
                                onClick = onInfoClick,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.75f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = ComicIcons.MoreVert,
                                    contentDescription = "ファイルの情報を開く",
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Absolute.spacedBy(4.dp)
                        ) {
                            AssistChip(
                                onClick = {},
                                leadingIcon = {
                                    Icon(
                                        if (bookshelfFolder.bookshelf is SmbServer) ComicIcons.Dns else ComicIcons.SdStorage,
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(text = if (bookshelfFolder.bookshelf is SmbServer) "Smb" else "Device")
                                }
                            )
                            AssistChip(
                                onClick = {},
                                leadingIcon = {
                                    Icon(
                                        ComicIcons.Folder,
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text(text = bookshelfFolder.bookshelf.fileCount.toString())
                                }
                            )
                        }
                    }
                }
            } else {
                Column {
                    Box {
                        AsyncImage2(
                            model = bookshelfFolder.folder,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            loading = {
                                Icon(
                                    imageVector = ComicIcons.DocumentUnknown,
                                    contentDescription = null
                                )
                            },
                            error = {
                                Icon(imageVector = ComicIcons.Image, contentDescription = null)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(
                                    ComicTheme.colorScheme.surfaceVariant,
                                    shape = CardDefaults.shape
                                )
                                .clip(CardDefaults.shape),
                        )
                        IconButton(
                            onClick = onInfoClick,
                            modifier = Modifier.align(Alignment.BottomEnd),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.75f
                                ),
                                contentColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Icon(
                                imageVector = ComicIcons.MoreVert,
                                contentDescription = "ファイルの情報を開く",
                            )
                        }
                    }
                    Text(
                        text = BookshelfConverter.displayName(
                            bookshelfFolder.bookshelf,
                            bookshelfFolder.folder
                        ),
                        style = ComicTheme.typography.bodyMedium.copy(
                            letterSpacing = 0.sp
                        ),
                        maxLines = 2,
                        minLines = 2,
                        modifier = Modifier
                            .padding(8.dp),
                    )
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Absolute.spacedBy(4.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    if (bookshelfFolder.bookshelf is SmbServer) ComicIcons.Dns else ComicIcons.SdStorage,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = if (bookshelfFolder.bookshelf is SmbServer) "Smb" else "Device")
                            }
                        )
                        AssistChip(
                            onClick = {},
                            leadingIcon = {
                                Icon(
                                    ComicIcons.Folder,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(text = bookshelfFolder.bookshelf.fileCount.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewBookshelf() {
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
