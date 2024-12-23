package com.sorrowblue.comicviewer.bookshelf.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.file.component.FileThumbnailAsyncImage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.imageBackground
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fakeSmbServer

@Composable
internal fun BookshelfListItem(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints {
        if (maxWidth > 360.dp) {
            BookshelfRowItem(bookshelfFolder, onClick, onInfoClick, modifier)
        } else {
            BookshelfColumnItem(bookshelfFolder, onClick, onInfoClick, modifier)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookshelfColumnItem(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CardDefaults.cardColors()
    Card(onClick = onClick, modifier = modifier, colors = colors) {
        FileThumbnailAsyncImage(
            fileThumbnail = FolderThumbnail.from(bookshelfFolder.folder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(CardDefaults.shape)
                .background(ComicTheme.colorScheme.imageBackground(colors.containerColor))
        )
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                Text(text = bookshelfFolder.displayName, maxLines = 2, minLines = 2)
            },
            supportingContent = {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.targetSpacing),
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
                            Icon(ComicIcons.Folder, contentDescription = null)
                        },
                        label = {
                            Text(text = bookshelfFolder.bookshelf.fileCount.toString())
                        }
                    )
                    Spacer(Modifier.weight(1f))
                    OutlinedIconButton(
                        onClick = onInfoClick,
                    ) {
                        Icon(ComicIcons.MoreVert, null)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookshelfRowItem(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CardDefaults.cardColors()
    Card(onClick = onClick, colors = colors, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            var size by remember { mutableStateOf(Dp.Unspecified) }
            FileThumbnailAsyncImage(
                fileThumbnail = FolderThumbnail.from(bookshelfFolder.folder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CardDefaults.shape)
                    .background(ComicTheme.colorScheme.imageBackground(colors.containerColor)),
            )
            val density = LocalDensity.current
            ListItem(
                modifier = Modifier
                    .onGloballyPositioned {
                        with(density) {
                            size = it.size.height.toDp()
                        }
                    },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = {
                    Text(text = bookshelfFolder.displayName, maxLines = 2, minLines = 2)
                },
                supportingContent = {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        itemVerticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
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
                                Icon(ComicIcons.Folder, contentDescription = null)
                            },
                            label = {
                                Text(text = bookshelfFolder.bookshelf.fileCount.toString())
                            }
                        )
                    }
                },
                trailingContent = {
                    IconButton(onClick = onInfoClick) {
                        Icon(ComicIcons.MoreVert, null)
                    }
                }
            )
        }
    }
}

private val BookshelfFolder.displayName
    get() = when (val bookshelf = bookshelf) {
        is InternalStorage -> bookshelf.displayName.ifEmpty { folder.name }
        is SmbServer -> bookshelf.displayName.ifEmpty { bookshelf.host }
        ShareContents -> bookshelf.displayName.ifEmpty { bookshelf.displayName }
    }

@Preview(widthDp = 360, apiLevel = 34, device = Devices.PIXEL_7)
@Preview(widthDp = 400, apiLevel = 34, device = Devices.PIXEL_7)
@Preview(
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    apiLevel = 34,
    device = Devices.PIXEL_7
)
@Preview(
    widthDp = 400,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    apiLevel = 34,
    device = Devices.PIXEL_7
)
@Composable
private fun BookshelfCardPreview() {
    PreviewTheme {
        val bookshelfFolder = BookshelfFolder(fakeSmbServer(), fakeFolder())
        BookshelfListItem(
            bookshelfFolder = bookshelfFolder,
            onClick = {},
            onInfoClick = {},
        )
    }
}
