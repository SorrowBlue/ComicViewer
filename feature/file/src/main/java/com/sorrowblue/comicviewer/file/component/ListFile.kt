package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

/**
 * ファイル情報をリストアイテムで表示する
 *
 * @param file　ファイル
 * @param onClick　クリック時の処理
 * @param onLongClick　ロングクリック時の処理
 * @param modifier Modifier
 * @param isThumbnailEnabled　サムネイル表示を有効にするか
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListFile(
    file: File,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    showThumbnail: Boolean = true,
    fontSize: Int,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
) {
    ListItem(
        modifier = modifier.combinedClickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onLongClick = onLongClick,
            onClick = onClick
        ),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(file.name)
        },
        supportingContent = {
            if (file is Book && 0 < file.lastPageRead) {
                LinearProgressIndicator(
                    progress = { file.lastPageRead.toFloat() / file.totalPageCount },
                )
            }
        },
        leadingContent = {
            if (showThumbnail) {
                SubcomposeAsyncImage(
                    model = file,
                    contentDescription = null,
                    contentScale = contentScale,
                    filterQuality = filterQuality,
                    loading = { CircularProgressIndicator(Modifier.wrapContentSize()) },
                    error = {
                        Icon(
                            imageVector = if (file is Book) ComicIcons.Image else ComicIcons.Folder,
                            contentDescription = null,
                            modifier = Modifier
                                .wrapContentSize()
                                .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                        )
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CardDefaults.shape)
                        .background(ComicTheme.colorScheme.surfaceContainer),
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
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
        trailingContent = {
            if (file is Book && 0 < file.totalPageCount) {
                Text("${file.totalPageCount}", fontSize = fontSize.sp)
            }
        }
    )
}

/**
 * ファイル情報をカードで表示する
 *
 * @param file ファイル
 * @param onClick　クリック時の処理
 * @param onLongClick　ロングクリック時の処理
 * @param modifier Modifier
 * @param isThumbnailEnabled サムネイル表示を有効にするか
 */
@Composable
fun ListFileCard(
    file: File,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    showThumbnail: Boolean,
    fontSize: Int,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
) {
    Card(onClick = onClick, modifier = modifier) {
        ListItem(
            headlineContent = {
                Text(file.name, fontSize = fontSize.sp)
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            supportingContent = {
                if (file is Book && 0 < file.lastPageRead) {
                    LinearProgressIndicator(
                        progress = { file.lastPageRead.toFloat() / file.totalPageCount },
                    )
                }
            },
            leadingContent = {
                if (showThumbnail) {
                    SubcomposeAsyncImage(
                        model = file,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CardDefaults.shape)
                            .background(ComicTheme.colorScheme.surfaceContainer),
                        contentDescription = null,
                        contentScale = contentScale,
                        filterQuality = filterQuality,
                        loading = { CircularProgressIndicator(Modifier.wrapContentSize()) },
                        error = {
                            Icon(
                                imageVector = if (file is Book) ComicIcons.Image else ComicIcons.Folder,
                                contentDescription = null,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                            )
                        },
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CardDefaults.shape)
                            .background(ComicTheme.colorScheme.surfaceVariant),
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
            trailingContent = {
                IconButton(onClick = onLongClick) {
                    Icon(
                        imageVector = ComicIcons.MoreVert,
                        contentDescription = stringResource(R.string.file_desc_open_file_info)
                    )
                }
            }
        )
    }
}
