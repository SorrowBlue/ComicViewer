package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme

@Composable
internal fun BookshelfImageCacheInfoItem(
    imageCacheInfo: BookshelfImageCacheInfo,
    onClick: () -> Unit,
) {
    Card {
        Text(
            modifier = Modifier.padding(ComicTheme.dimension.padding),
            text = imageCacheInfo.bookshelf.displayName,
            style = ComicTheme.typography.bodyLarge
        )
        Column {
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(ComicIcons.Folder),
                        contentDescription = null
                    )
                },
                headlineContent = {
                },
                supportingContent = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = imageCacheInfo.bookshelf.displayName)
                        Text(
                            modifier = Modifier.align(Alignment.End),
                            style = ComicTheme.typography.bodySmall,
                            text = "${imageCacheInfo.size.megaByte}MB / ${imageCacheInfo.maxSize.megaByte}MB"
                        )
                        val color = ProgressIndicatorDefaults.linearColor
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth(),
                            strokeCap = StrokeCap.Butt,
                            gapSize = 0.dp,
                            progress = { imageCacheInfo.size.toFloat() / imageCacheInfo.maxSize },
                            drawStopIndicator = {
                                drawStopIndicator(
                                    drawScope = this,
                                    stopSize = 0.dp,
                                    color = color,
                                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                                )
                            }
                        )
                        Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
                        Button(onClick = onClick, modifier = Modifier.align(Alignment.End)) {
                            Text(text = "キャッシュを削除")
                        }
                    }
                }
            )
            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                leadingContent = {
                    Icon(
                        painter = rememberVectorPainter(ComicIcons.Book),
                        contentDescription = null
                    )
                },
                headlineContent = {},
                supportingContent = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = imageCacheInfo.bookshelf.displayName)
                        Text(
                            modifier = Modifier.align(Alignment.End),
                            style = ComicTheme.typography.bodySmall,
                            text = "${imageCacheInfo.size.megaByte}MB / ${imageCacheInfo.maxSize.megaByte}MB"
                        )
                        val color = ProgressIndicatorDefaults.linearColor
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth(),
                            strokeCap = StrokeCap.Butt,
                            gapSize = 0.dp,
                            progress = { imageCacheInfo.size.toFloat() / imageCacheInfo.maxSize },
                            drawStopIndicator = {
                                drawStopIndicator(
                                    drawScope = this,
                                    stopSize = 0.dp,
                                    color = color,
                                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                                )
                            }
                        )
                        Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
                        Button(onClick = onClick, modifier = Modifier.align(Alignment.End)) {
                            Text(text = "キャッシュを削除")
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun BookshelfImageCacheInfoItemPreview() {
    PreviewTheme {
        BookshelfImageCacheInfoItem(
            imageCacheInfo = BookshelfImageCacheInfo(
                InternalStorage("Display name"),
                BookshelfImageCacheInfo.Type.Thumbnail,
                50 * 1024 * 1024,
                100 * 1024 * 1024
            ),
            onClick = {}
        )
    }
}
