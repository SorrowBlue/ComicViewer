package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.feature.file.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.preview.fakeBookFile
import com.sorrowblue.comicviewer.framework.preview.previewPainter

/**
 * ファイル情報をグリッドアイテムで表示する
 *
 * @param file ファイル
 * @param onClick クリック時の処理
 * @param onInfoClick インフォクリック時の処理
 * @param showThumbnail サムネイル表示を有効にするか
 * @param modifier Modifier
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
) {
    ElevatedCard(onClick = onClick, modifier = modifier) {
        Box {
            if (showThumbnail) {
                GridFileThumbnail(
                    file = file,
                    contentScale = contentScale,
                    filterQuality = filterQuality
                )
            } else {
                GridFileIcon(file = file)
            }
            IconButton(
                onClick = onInfoClick,
                modifier = Modifier.align(Alignment.BottomEnd),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ComicTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    contentColor = ComicTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = ComicIcons.MoreVert,
                    contentDescription = stringResource(R.string.file_desc_open_file_info),
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (file is Folder) {
                Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
                Icon(imageVector = ComicIcons.Folder, contentDescription = null)
            }
            Box {
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
                        .padding(ComicTheme.dimension.padding)
                )
                if (file is Book && 0 < file.lastPageRead) {
                    LinearProgressIndicator(
                        progress = { file.lastPageRead.toFloat() / file.totalPageCount },
                        modifier = Modifier
                            .fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun GridFileThumbnail(
    file: File,
    contentScale: ContentScale,
    filterQuality: FilterQuality,
) {
    SubcomposeAsyncImage(
        model = file,
        contentDescription = null,
        alignment = Alignment.TopCenter,
        contentScale = contentScale,
        filterQuality = filterQuality,
        loading = {
            CircularProgressIndicator(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center)
            )
        },
        error = {
            if (LocalInspectionMode.current) {
                Image(painter = previewPainter(), contentDescription = null)
            } else {
                Icon(
                    imageVector = if (file is Book) ComicIcons.BrokenImage else ComicIcons.FolderOff,
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentSize()
                        .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                        .align(Alignment.Center)
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CardDefaults.shape),
    )
}

@Composable
private fun GridFileIcon(file: File) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(CardDefaults.shape)
            .background(ComicTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (file is Book) {
            Icon(
                imageVector = ComicIcons.Book,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ComicTheme.dimension.margin)
            )
        } else {
            Icon(
                imageVector = ComicIcons.Folder,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ComicTheme.dimension.margin)
            )
        }
    }
}

@Preview(widthDp = 120)
@Preview(widthDp = 160)
@Preview(widthDp = 180)
@Preview(widthDp = 200)
@Composable
private fun PreviewFileGrid() {
    PreviewTheme {
        GridFile(
            file = fakeBookFile(),
            onClick = {},
            onInfoClick = {},
            showThumbnail = true,
            fontSize = FolderDisplaySettingsDefaults.fontSize,
            contentScale = ContentScale.Fit,
            filterQuality = FilterQuality.None
        )
    }
}
