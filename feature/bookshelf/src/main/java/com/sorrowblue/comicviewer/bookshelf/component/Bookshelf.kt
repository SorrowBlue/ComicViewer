package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.fakeFolder
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.rememberDebugPlaceholder

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun Bookshelf(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val thumbnail = remember {
        movableContentOf {
            SubcomposeAsyncImage(
                model = bookshelfFolder.folder,
                contentDescription = stringResource(R.string.bookshelf_desc_thumbnail),
                contentScale = ContentScale.Crop,
                loading = {
                    if (LocalInspectionMode.current) {
                        Image(painter = rememberDebugPlaceholder()!!, contentDescription = null)
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center)
                        )
                    }
                },
                error = {
                    Icon(
                        imageVector = ComicIcons.FolderOff,
                        contentDescription = null,
                        modifier = Modifier
                            .wrapContentSize()
                            .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                            .align(Alignment.Center)
                    )
                },
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(CardDefaults.shape)
                    .background(ComicTheme.colorScheme.surfaceVariant, CardDefaults.shape),
            )
        }
    }
    val displayName = remember {
        movableContentOf<Modifier> { modifier ->
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
                    .then(modifier)
            )
        }
    }
    val info = remember {
        movableContentOf {
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
                    contentDescription = stringResource(R.string.bookshelf_desc_open_bookshelf_info),
                )
            }
        }
    }
    val chips = remember {
        movableContentOf {
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
                        Icon(ComicIcons.Folder, contentDescription = null)
                    },
                    label = {
                        Text(text = bookshelfFolder.bookshelf.fileCount.toString())
                    }
                )
            }
        }
    }
    ElevatedCard(
        modifier = modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick,
        )
    ) {
        BoxWithConstraints {
            if (300.dp < maxWidth) {
                Row(modifier = Modifier.height(160.dp)) {
                    thumbnail()
                    Column {
                        Row {
                            displayName(Modifier.weight(1f))
                            info()
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        chips()
                    }
                }
            } else {
                Column {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        thumbnail()
                        info()
                    }
                    displayName(Modifier)
                    chips()
                }
            }
        }
    }
}

@Preview(widthDp = 200)
@Preview
@Composable
private fun PreviewBookshelf() {
    PreviewTheme {
        Bookshelf(
            bookshelfFolder = BookshelfFolder(
                InternalStorage(BookshelfId(0), "display name", 0),
                fakeFolder()
            ),
            {},
            {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
