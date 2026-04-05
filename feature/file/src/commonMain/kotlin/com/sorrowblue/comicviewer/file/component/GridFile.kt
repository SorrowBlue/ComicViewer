package com.sorrowblue.comicviewer.file.component

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFolder
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_desc_open_file_info
import org.jetbrains.compose.resources.stringResource

/**
 * ファイル情報をグリッドアイテムで表示する
 *
 * @param file ファイル
 * @param onClick クリック時の処理
 * @param onInfoClick インフォクリック時の処理
 * @param showThumbnail サムネイル表示を有効にするか
 * @param modifier Modifier
 */
@OptIn(ExperimentalSharedTransitionApi::class)
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
                    val boundsTransform = ComicTheme.motionScheme.slowSpatialSpec<Rect>()
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
            if (file is Folder) {
                AssistChip(
                    onClick = {},
                    label = { Text("Folder") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = ComicTheme.colorScheme.secondaryContainer.copy(
                            alpha = 0.65f,
                        ),
                    ),
                    modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp),
                )
            }
            IconButton(
                onClick = onInfoClick,
                modifier = Modifier.align(Alignment.TopEnd)
                    .testTag("FileListItemMenu"),
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
        Text(
            text = file.name,
            style = ComicTheme.typography.bodyLarge.copy(
                fontSize = fontSize.sp,
                lineHeight = (fontSize + 4).sp,
            ),
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            minLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(ComicTheme.dimension.padding),
        )
        if (file is Folder) {
            Text(
                text = "${file.count} Items",
                style = ComicTheme.typography.labelSmall.copy(),
                color = ComicTheme.colorScheme.onSurface,
            )
        }
        if (file is Book) {
            Row {
                Text(
                    text = if ((file.lastPageRead + 1).toFloat() / file.totalPageCount ==
                        1f
                    ) {
                        "Completed"
                    } else {
                        "${((file.lastPageRead + 1).toFloat() / file.totalPageCount * 100).toInt()}% Read"
                    },
                    style = ComicTheme.typography.labelSmall.copy(),
                    color = ComicTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${if (file.lastPageRead == 0) "-" else file.lastPageRead + 1} / ${file.totalPageCount}",
                    style = ComicTheme.typography.labelSmall.copy(),
                    color = ComicTheme.colorScheme.onSurface,
                )
            }
            val color = ProgressIndicatorDefaults.linearColor
            LinearProgressIndicator(
                progress = { (file.lastPageRead + 1).toFloat() / file.totalPageCount },
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
                modifier = Modifier
                    .fillMaxWidth(),
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

@Preview(widthDp = 120, showBackground = true)
@Preview(widthDp = 160, showBackground = true)
@Preview(widthDp = 180, showBackground = true)
@Preview(widthDp = 200, showBackground = true)
@Composable
internal fun FileGridPreview() {
    PreviewTheme {
        GridFile(
            file = fakeBookFile(),
            onClick = {},
            onInfoClick = {},
            showThumbnail = true,
            fontSize = FolderDisplaySettingsDefaults.FontSize,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None,
        )
    }
}

@Preview(widthDp = 120, showBackground = true)
@Preview(widthDp = 160, showBackground = true)
@Preview(widthDp = 180, showBackground = true)
@Preview(widthDp = 200, showBackground = true)
@Composable
internal fun FileGridFolderPreview() {
    PreviewTheme {
        GridFile(
            file = fakeFolder(),
            onClick = {},
            onInfoClick = {},
            showThumbnail = true,
            fontSize = FolderDisplaySettingsDefaults.FontSize,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None,
        )
    }
}
