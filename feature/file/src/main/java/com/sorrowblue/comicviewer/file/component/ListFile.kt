package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.imageBackground
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile

/**
 * ファイル情報をリストアイテムで表示する
 *
 * @param file ファイル
 * @param onClick クリック時の処理
 * @param onLongClick ロングクリック時の処理
 * @param showThumbnail サムネイル表示するか
 * @param fontSize
 * @param contentScale
 * @param filterQuality
 * @param modifier Modifier
 */
@Composable
fun ListFile(
    file: File,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showThumbnail: Boolean,
    fontSize: Int,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            if (showThumbnail) {
                FileThumbnailAsyncImage(
                    fileThumbnail = FileThumbnail.from(file),
                    contentScale = contentScale,
                    filterQuality = filterQuality,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CardDefaults.shape)
                        .background(ComicTheme.colorScheme.imageBackground(ListItemDefaults.containerColor))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CardDefaults.shape)
                        .background(ComicTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center
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
            Row {
                if (file is Folder) {
                    Icon(imageVector = ComicIcons.Folder, contentDescription = null)
                    Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
                }
                Text(text = file.name, fontSize = fontSize.sp)
            }
        },
        supportingContent = {
            Column {
                if (file is Book && 0 < file.lastPageRead) {
                    val color = ProgressIndicatorDefaults.linearColor
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        progress = { file.lastPageRead.toFloat() / file.totalPageCount },
                        strokeCap = StrokeCap.Butt,
                        gapSize = 0.dp,
                        drawStopIndicator = {
                            drawStopIndicator(
                                drawScope = this,
                                stopSize = 0.dp,
                                color = color,
                                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                            )
                        }
                    )
                }
            }
        },
        trailingContent = {
            IconButton(onClick = onLongClick) {
                Icon(
                    imageVector = ComicIcons.MoreVert,
                    contentDescription = stringResource(R.string.file_desc_open_file_info)
                )
            }
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}

/**
 * ファイル情報をカードで表示する ファイル情報をリストアイテムで表示する
 *
 * @param file ファイル
 * @param onClick クリック時の処理
 * @param onLongClick ロングクリック時の処理
 * @param showThumbnail サムネイル表示するか
 * @param fontSize
 * @param contentScale
 * @param filterQuality
 * @param modifier Modifier
 */
@Composable
fun ListFileCard(
    file: File,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showThumbnail: Boolean,
    fontSize: Int,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
    modifier: Modifier = Modifier,
) {
    val colors = CardDefaults.cardColors()
    Card(onClick = onClick, colors = colors, modifier = modifier) {
        ListItem(
            leadingContent = {
                if (showThumbnail) {
                    FileThumbnailAsyncImage(
                        fileThumbnail = FileThumbnail.from(file),
                        contentScale = contentScale,
                        filterQuality = filterQuality,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CardDefaults.shape)
                            .background(ComicTheme.colorScheme.imageBackground(colors.containerColor))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp),
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
                Text(file.name, fontSize = fontSize.sp)
            },
            supportingContent = {
                if (file is Book && 0 < file.lastPageRead) {
                    LinearProgressIndicator(
                        progress = { file.lastPageRead.toFloat() / file.totalPageCount },
                    )
                }
            },
            trailingContent = {
                IconButton(onClick = onLongClick) {
                    Icon(
                        imageVector = ComicIcons.MoreVert,
                        contentDescription = stringResource(R.string.file_desc_open_file_info)
                    )
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewFileList(
    @PreviewParameter(BooleanProvider::class) showThumbnail: Boolean,
) {
    PreviewTheme {
        ListFile(
            file = fakeBookFile(name = "Fake book name"),
            onClick = {},
            onLongClick = {},
            showThumbnail = showThumbnail,
            fontSize = FolderDisplaySettingsDefaults.fontSize,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewFileListCard(
    @PreviewParameter(BooleanProvider::class) showThumbnail: Boolean,
) {
    PreviewTheme {
        ListFileCard(
            file = fakeBookFile(name = "Fake book name"),
            onClick = {},
            onLongClick = {},
            showThumbnail = showThumbnail,
            fontSize = FolderDisplaySettingsDefaults.fontSize,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None
        )
    }
}

private class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(true, false)
}
