package com.sorrowblue.comicviewer.feature.bookshelf.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.DeviceStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.file.component.FileThumbnailAsyncImage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.imageBackground
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeDeviceStorage
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeSmbServer
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_device
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_smb
import kotlin.random.Random
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfListItem(
    bookshelfFolder: BookshelfFolder,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CardDefaults.elevatedCardColors()
    ElevatedCard(
        onClick = onClick,
        colors = colors,
        modifier = modifier.testTag("BookshelfListItem")
    ) {
        Box {
            FileThumbnailAsyncImage(
                fileThumbnail = FolderThumbnail.from(bookshelfFolder.folder),
                contentScale = ContentScale.Crop,
                error = null,
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                0f to colors.containerColor.copy(alpha = 1f),
                                0.2f to colors.containerColor.copy(alpha = 0.8f),
                                0.5f to colors.containerColor.copy(alpha = 0.5f),
                                1f to colors.containerColor.copy(alpha = 0.0f),
                            ),
                            blendMode = BlendMode.DstIn,
                        )
                    }
                    .clip(CardDefaults.shape)
                    .background(ComicTheme.colorScheme.imageBackground(colors.containerColor)),
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Card(modifier = Modifier.size(60.dp)) {
                        Spacer(Modifier.weight(1f))
                        Icon(
                            imageVector = if (bookshelfFolder.bookshelf is SmbServer) ComicIcons.Dns else ComicIcons.SdStorage,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally),
                        )
                        Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.size(ComicTheme.dimension.padding))
                    Column {
                        Row {
                            AssistChip(
                                onClick = {},
                                label = {
                                    Text(
                                        when (bookshelfFolder.bookshelf) {
                                            is DeviceStorage -> stringResource(
                                                Res.string.bookshelf_label_device,
                                            )

                                            is SmbServer -> stringResource(
                                                Res.string.bookshelf_label_smb,
                                            )

                                            ShareContents -> ""
                                        },
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = ComicTheme.colorScheme.primaryContainer,
                                    labelColor = ComicTheme.colorScheme.primary,
                                ),
                            )
                            Spacer(Modifier.weight(1f))
                            OutlinedIconButton(
                                onClick = onInfoClick,
                                modifier = Modifier.testTag("BookshelfListItemMenu"),
                            ) {
                                Icon(ComicIcons.MoreVert, null)
                            }
                        }
                        Text(
                            text = bookshelfFolder.displayName,
                            maxLines = 2,
                            minLines = 2,
                            style = ComicTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = colors.contentColor,
                            modifier = Modifier
                                .fillMaxWidth(),
                        )
                    }
                }
                Spacer(Modifier.size(16.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "FILES",
                            style = ComicTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = colors.contentColor,
                        )
                        Text(
                            bookshelfFolder.bookshelf.fileCount.toString(),
                            style = ComicTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = ComicTheme.colorScheme.primary,
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    val connected by remember { mutableStateOf(Random.nextBoolean()) }
                    Text(
                        if (connected) "Connected ●" else "Offline -",
                        style = ComicTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (connected) {
                            ComicTheme.colorScheme.primary
                        } else {
                            ComicTheme.colorScheme.primary.copy(
                                alpha = 0.65f,
                            )
                        },
                    )
                }
            }
        }
    }
}

private val BookshelfFolder.displayName
    get() = when (val bookshelf = bookshelf) {
        is DeviceStorage -> bookshelf.displayName.ifEmpty { folder.name }
        is SmbServer -> bookshelf.displayName.ifEmpty { bookshelf.host }
        ShareContents -> bookshelf.displayName.ifEmpty { bookshelf.displayName }
    }

@Preview
@Preview(uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@Composable
private fun BookshelfCardPreview(
    @PreviewParameter(
        BookshelfFolderProvider::class,
    ) bookshelfFolder: BookshelfFolder,
) {
    PreviewTheme {
        BookshelfListItem(
            bookshelfFolder = bookshelfFolder,
            onClick = {},
            onInfoClick = {},
        )
    }
}

private class BookshelfFolderProvider : PreviewParameterProvider<BookshelfFolder> {
    override val values = sequenceOf(
        BookshelfFolder(fakeSmbServer(), fakeFolder()),
        BookshelfFolder(fakeDeviceStorage(), fakeFolder()),
    )

    override fun getDisplayName(index: Int): String? = values.toList()[index].bookshelf.type?.name
}
