package com.sorrowblue.comicviewer.bookshelf.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.file.component.FileThumbnailAsyncImage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreenDarkFixedWidth
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreenFixedWidth
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fakeSmbServer

@PreviewMultiScreenFixedWidth
@PreviewMultiScreenDarkFixedWidth
@Composable
private fun BookshelfCardPreview() {
    PreviewTheme {
        val navigationState = LocalNavigationState.current
        val bookshelfFolder = BookshelfFolder(fakeSmbServer(), fakeFolder())
        BookshelfCard(
            state = navigationState,
            bookshelfFolder = bookshelfFolder,
            onClick = {},
            onInfoClick = {}
        )
    }
}

@Composable
fun BookshelfCard(
    state: NavigationState,
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state is NavigationState.NavigationBar) {
        BookshelfRowCard(
            bookshelfFolder = bookshelfFolder,
            onClick = onClick,
            onInfoClick = onInfoClick,
            modifier = modifier
        )
    } else {
        BookshelfColumnCard(
            bookshelfFolder = bookshelfFolder,
            onClick = onClick,
            onInfoClick = onInfoClick,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookshelfColumnCard(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier) {
        FileThumbnailAsyncImage(
            fileThumbnail = FolderThumbnail.from(bookshelfFolder.folder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(CardDefaults.shape)
                .background(ComicTheme.colorScheme.surfaceContainerHigh)
        )
        Text(
            text = BookshelfConverter.displayName(
                bookshelfFolder.bookshelf,
                bookshelfFolder.folder
            ),
            style = ComicTheme.typography.titleLarge.copy(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            minLines = 2,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = ComicTheme.dimension.targetSpacing),
            horizontalArrangement = Arrangement.Absolute.spacedBy(ComicTheme.dimension.targetSpacing)
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
                Icon(
                    imageVector = ComicIcons.MoreVert,
                    contentDescription = stringResource(R.string.bookshelf_desc_open_bookshelf_info),
                )
            }
        }
        Spacer(Modifier.size(16.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BookshelfRowCard(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            var size by remember { mutableStateOf(140.dp) }
            FileThumbnailAsyncImage(
                fileThumbnail = FolderThumbnail.from(bookshelfFolder.folder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(min(size, 120.dp), size)
                    .align(Alignment.Top)
                    .clip(CardDefaults.shape)
                    .background(ComicTheme.colorScheme.surfaceContainerHigh),
            )
            val density = LocalDensity.current
            Column(
                modifier = Modifier
                    .onGloballyPositioned {
                        with(density) {
                            size = it.size.height.toDp()
                        }
                    },
                verticalArrangement = Arrangement.Center
            ) {
                Row {
                    Text(
                        text = BookshelfConverter.displayName(
                            bookshelfFolder.bookshelf,
                            bookshelfFolder.folder
                        ),
                        style = ComicTheme.typography.bodyLarge,
                        maxLines = 2,
                        minLines = 2,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 16.dp, start = 16.dp, end = 8.dp)
                    )
                    IconButton(
                        onClick = onInfoClick,
                        modifier = Modifier.padding(top = 8.dp, end = 8.dp)
                    ) {
                        Icon(
                            imageVector = ComicIcons.MoreVert,
                            contentDescription = stringResource(R.string.bookshelf_desc_open_bookshelf_info),
                        )
                    }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.Absolute.spacedBy(ComicTheme.dimension.targetSpacing),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp)
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
    }
}
