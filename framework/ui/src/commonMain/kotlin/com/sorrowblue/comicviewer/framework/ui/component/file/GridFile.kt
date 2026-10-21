/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.component.file

import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.animation.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut
import comicviewer.framework.ui.generated.resources.Res
import comicviewer.framework.ui.generated.resources.file_desc_open_file_info
import comicviewer.framework.ui.generated.resources.file_label_file_count
import comicviewer.framework.ui.generated.resources.file_label_folder
import comicviewer.framework.ui.generated.resources.file_label_read_completed
import comicviewer.framework.ui.generated.resources.file_label_read_percent
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Displays file information as a grid item.
 *
 * @param file The file to display.
 * @param onClick Callback invoked when the item is clicked.
 * @param onInfoClick Callback invoked when the info button is clicked.
 * @param showThumbnail Whether to display the thumbnail.
 * @param fontSize Font size for the file name text.
 * @param contentScale Content scale for the thumbnail image.
 * @param filterQuality Filter quality for the thumbnail image.
 * @param modifier Modifier to be applied to the layout.
 * @param colors Colors for the card.
 */
@Composable
fun GridFile(
    file: File,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    showThumbnail: Boolean,
    fontSize: Int,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors().copy(containerColor = Color.Transparent),
) {
    Card(
        onClick = onClick,
        colors = colors,
        modifier = modifier,
        shape = AbsoluteCutCornerShape(0),
    ) {
        val boundsTransform = ComicTheme.motionScheme.slowSpatialSpec<Rect>()
        Box {
            with(LocalSharedTransitionScope.current) {
                if (showThumbnail) {
                    FileThumbnailAsyncImage(
                        fileThumbnail = FileThumbnail.from(file),
                        alignment = Alignment.TopCenter,
                        contentScale = contentScale,
                        filterQuality = filterQuality,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState("${file.bookshelfId}:${file.path}"),
                                LocalNavAnimatedContentScope.current,
                                enter = materialFadeThroughIn(),
                                exit = materialFadeThroughOut(),
                                boundsTransform = { _, _ -> boundsTransform },
                                resizeMode = scaleToBounds(contentScale, Alignment.Center),
                            ).fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        0f to colors.containerColor.copy(alpha = 1f),
                                        0.8f to colors.containerColor.copy(alpha = 0.6f),
                                        1f to colors.containerColor.copy(alpha = 0.0f),
                                    ),
                                    blendMode = BlendMode.DstIn,
                                )
                            }
                            .clip(CardDefaults.shape),
                    )
                } else {
                    GridFileIcon(
                        file = file,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState("${file.bookshelfId}:${file.path}"),
                                LocalNavAnimatedContentScope.current,
                                enter = materialFadeThroughIn(),
                                exit = materialFadeThroughOut(),
                                boundsTransform = { _, _ -> boundsTransform },
                                resizeMode = scaleToBounds(contentScale, Alignment.Center),
                            ),
                    )
                }
            }
            AssistChip(
                onClick = {},
                label = { Text(stringResource(Res.string.file_label_folder)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = ComicTheme.colorScheme.secondaryContainer.copy(
                        alpha = 0.65f,
                    ),
                ),
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp)
                    .visible(file is Folder),
            )
            IconButton(
                onClick = onInfoClick,
                modifier = Modifier.testTag("FileListItemMenu").align(Alignment.TopEnd),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ComicTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    contentColor = ComicTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Icon(
                    imageVector = ComicIcons.MoreVert,
                    contentDescription = stringResource(Res.string.file_desc_open_file_info),
                )
            }
        }
        FileNameText(
            name = file.name,
            fontSize = fontSize,
        )
        if (file is Folder) {
            FolderCountText(count = file.count)
        }
        if (file is Book) {
            val isRead = file.totalPageCount > 0 && file.lastReadTime > 0L
            val isCompleted = isRead && file.lastPageRead + 1 >= file.totalPageCount
            Row {
                BookReadStatusText(
                    book = file,
                    isRead = isRead,
                    isCompleted = isCompleted,
                )
                Spacer(Modifier.weight(1f))
                BookPageCountText(book = file)
            }
            BookProgressIndicator(
                book = file,
                visible = isRead,
            )
        }
    }
}

@Composable
private fun GridFileIcon(file: File, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CardDefaults.shape)
            .background(ComicTheme.colorScheme.surfaceVariant),
    ) {
        if (file is Book) {
            Icon(
                imageVector = ComicIcons.Book,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ComicTheme.dimension.margin),
            )
        } else {
            Icon(
                imageVector = ComicIcons.Folder,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ComicTheme.dimension.margin),
            )
        }
    }
}

@Composable
private fun FileNameText(name: String, fontSize: Int, modifier: Modifier = Modifier) {
    Text(
        text = name,
        style = ComicTheme.typography.bodyLarge.copy(
            fontSize = fontSize.sp,
            lineHeight = (fontSize + 4).sp,
        ),
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        minLines = 2,
        modifier = modifier
            .fillMaxWidth()
            .padding(ComicTheme.dimension.padding),
    )
}

@Composable
internal fun FolderCountText(count: Int, modifier: Modifier = Modifier) {
    Text(
        text = pluralStringResource(
            Res.plurals.file_label_file_count,
            count,
            count,
        ),
        style = ComicTheme.typography.labelSmall,
        color = ComicTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

@Composable
internal fun BookReadStatusText(
    book: Book,
    isRead: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = if (isCompleted) {
            stringResource(Res.string.file_label_read_completed)
        } else {
            val percent = if (book.totalPageCount > 0) {
                ((book.lastPageRead + 1).toFloat() / book.totalPageCount * 100)
                    .toInt()
                    .coerceIn(0, 100)
            } else {
                0
            }
            stringResource(Res.string.file_label_read_percent, percent)
        },
        style = ComicTheme.typography.labelSmall,
        color = ComicTheme.colorScheme.onSurface,
        modifier = modifier.visible(isRead),
    )
}

@Composable
internal fun BookPageCountText(book: Book, modifier: Modifier = Modifier) {
    val text = when {
        book.totalPageCount <= 0 -> "- / -"
        book.lastReadTime == 0L -> "- / ${book.totalPageCount}"
        else -> "${minOf(book.lastPageRead + 1, book.totalPageCount)} / ${book.totalPageCount}"
    }
    Text(
        text = text,
        style = ComicTheme.typography.labelSmall,
        color = ComicTheme.colorScheme.onSurface,
        modifier = modifier,
    )
}

@Composable
internal fun BookProgressIndicator(book: Book, visible: Boolean, modifier: Modifier = Modifier) {
    val color = ProgressIndicatorDefaults.linearColor
    LinearProgressIndicator(
        progress = {
            if (book.totalPageCount > 0) {
                minOf((book.lastPageRead + 1).toFloat() / book.totalPageCount, 1f)
            } else {
                0f
            }
        },
        strokeCap = StrokeCap.Round,
        gapSize = 0.dp,
        drawStopIndicator = {
            drawStopIndicator(
                drawScope = this,
                stopSize = 0.dp,
                color = color,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .visible(visible),
    )
}
