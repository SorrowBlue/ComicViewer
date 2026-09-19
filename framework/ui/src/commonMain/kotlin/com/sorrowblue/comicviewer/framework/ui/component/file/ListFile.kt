/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.component.file

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.scaleToBounds
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.imageBackground
import com.sorrowblue.comicviewer.framework.ui.animation.LocalSharedTransitionScope
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut
import comicviewer.framework.ui.generated.resources.Res
import comicviewer.framework.ui.generated.resources.file_desc_open_file_info
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ListFile(
    file: File,
    onInfoClick: () -> Unit,
    showThumbnail: Boolean,
    fontSize: Int,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
    modifier: Modifier = Modifier,
    colors: ListItemColors = ListItemDefaults.colors(),
) {
    with(LocalSharedTransitionScope.current) {
        ListItem(
            leadingContent = {
                if (showThumbnail) {
                    Box {
                        val boundsTransform = ComicTheme.motionScheme.slowSpatialSpec<Rect>()
                        FileThumbnailAsyncImage(
                            fileThumbnail = FileThumbnail.from(file),
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
                                ).size(80.dp)
                                .clip(CardDefaults.shape)
                                .background(
                                    ComicTheme.colorScheme.imageBackground(
                                        ListItemDefaults.containerColor,
                                    ),
                                ),
                        )
                    }
                } else {
                    val boundsTransform = ComicTheme.motionScheme.slowSpatialSpec<Rect>()
                    Box(
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState("${file.bookshelfId}:${file.path}"),
                                LocalNavAnimatedContentScope.current,
                                enter = materialFadeThroughIn(),
                                exit = materialFadeThroughOut(),
                                boundsTransform = { _, _ -> boundsTransform },
                                resizeMode = scaleToBounds(contentScale, Alignment.Center),
                            ).size(80.dp)
                            .clip(CardDefaults.shape)
                            .background(ComicTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (file is Book) {
                            Icon(imageVector = ComicIcons.Book, contentDescription = null)
                        } else {
                            Icon(imageVector = ComicIcons.Folder, contentDescription = null)
                        }
                    }
                }
            },
            headlineContent = {
                Text(text = file.name, fontSize = fontSize.sp)
            },
            supportingContent = {
                if (file is Book && 0 < file.lastPageRead) {
                    val color = ProgressIndicatorDefaults.linearColor
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { file.lastPageRead.toFloat() / file.totalPageCount },
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
                    )
                }
            },
            trailingContent = {
                IconButton(onClick = onInfoClick, modifier = Modifier.testTag("FileListItemMenu")) {
                    Icon(
                        imageVector = ComicIcons.MoreVert,
                        contentDescription = stringResource(Res.string.file_desc_open_file_info),
                    )
                }
            },
            colors = colors,
            modifier = modifier,
        )
    }
}

/**
 * ファイル惁E��をカードで表示する ファイル惁E��をリストアイチE��で表示する
 *
 * @param file ファイル
 * @param onClick クリチE��時�E処琁E * @param onInfoClick 惁E��ボタンクリチE��時�E処琁E * @param showThumbnail サムネイル表示するぁE * @param fontSize
 * @param contentScale
 * @param filterQuality
 * @param modifier Modifier
 */
@Composable
fun ListFileCard(
    file: File,
    onClick: () -> Unit,
    onInfoClick: () -> Unit,
    showThumbnail: Boolean,
    fontSize: Int,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(), modifier = modifier) {
        ListFile(
            file = file,
            onInfoClick = onInfoClick,
            showThumbnail = showThumbnail,
            fontSize = fontSize,
            contentScale = contentScale,
            filterQuality = filterQuality,
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        )
    }
}
