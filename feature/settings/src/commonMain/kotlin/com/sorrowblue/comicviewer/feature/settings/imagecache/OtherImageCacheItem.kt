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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_imagecache_label_other
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun OtherImageCacheItem(
    imageCache: ImageCache,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsCategory(
        modifier = modifier,
        title = { Text(text = stringResource(Res.string.settings_imagecache_label_other)) },
    ) {
        Setting(title = {}, onClick = {}, widget = {
            IconButton(onClick = onClick) {
                Icon(imageVector = ComicIcons.Delete, contentDescription = null)
            }
        }, summary = {
            Column {
                Text(
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
        })
    }
}

@Preview
@Composable
private fun OtherImageCacheItemPreview() {
    PreviewTheme {
        OtherImageCacheItem(
            imageCache = OtherImageCache(12345, 12345600),
            onClick = {},
        )
    }
}
