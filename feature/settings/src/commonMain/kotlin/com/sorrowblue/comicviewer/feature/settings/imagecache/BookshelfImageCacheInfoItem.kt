package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeInternalStorage
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_imagecache_label_page
import comicviewer.feature.settings.generated.resources.settings_imagecache_label_thumbnail
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfImageCacheInfoItem(
    imageCacheInfo: BookshelfImageCacheInfo,
    onThumbnailImageCacheClick: () -> Unit,
    onBookPageImageCacheClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsCategory(
        modifier = modifier,
        title = {
            Text(text = imageCacheInfo.bookshelf.displayName)
        },
    ) {
        ThumbnailImageCacheItem(
            imageCache = imageCacheInfo.thumbnailImageCache,
            onClick = onThumbnailImageCacheClick,
        )
        BookPageImageCacheItem(
            imageCache = imageCacheInfo.bookPageImageCache,
            onClick = onBookPageImageCacheClick,
        )
    }
}

@Composable
private fun ThumbnailImageCacheItem(imageCache: ThumbnailImageCache, onClick: () -> Unit) {
    Setting(
        title = {
            Text(text = stringResource(Res.string.settings_imagecache_label_thumbnail))
        },
        onClick = {},
        summary = {
            Column {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    style = ComicTheme.typography.bodySmall,
                    text = "${imageCache.size.megaByte}MB / ${imageCache.maxSize.megaByte}MB",
                )
                val color = ProgressIndicatorDefaults.linearColor
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                    strokeCap = StrokeCap.Butt,
                    gapSize = 0.dp,
                    progress = { imageCache.size.toFloat() / imageCache.maxSize },
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
        widget = {
            IconButton(onClick = onClick) {
                Icon(imageVector = ComicIcons.Delete, contentDescription = null)
            }
        },
    )
}

@Composable
private fun BookPageImageCacheItem(imageCache: BookPageImageCache, onClick: () -> Unit) {
    Setting(
        title = {
            Text(text = stringResource(Res.string.settings_imagecache_label_page))
        },
        onClick = {},
        summary = {
            Column {
                Text(
                    modifier = Modifier.align(Alignment.End),
                    style = ComicTheme.typography.bodySmall,
                    text = "${imageCache.size.megaByte}MB / ${imageCache.maxSize.megaByte}MB",
                )
                val color = ProgressIndicatorDefaults.linearColor
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                    strokeCap = StrokeCap.Butt,
                    gapSize = 0.dp,
                    progress = { imageCache.size.toFloat() / imageCache.maxSize },
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
        widget = {
            IconButton(onClick = onClick) {
                Icon(imageVector = ComicIcons.Delete, contentDescription = null)
            }
        },
    )
}

@Preview
@Composable
private fun BookshelfImageCacheInfoItemPreview() {
    PreviewTheme {
        BookshelfImageCacheInfoItem(
            imageCacheInfo = BookshelfImageCacheInfo(
                fakeInternalStorage(),
                ThumbnailImageCache(123456, 1234567),
                BookPageImageCache(1234, 12345),
            ),
            onThumbnailImageCacheClick = {},
            onBookPageImageCacheClick = {},
        )
    }
}
