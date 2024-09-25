package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpace
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalComponentColors
import com.sorrowblue.comicviewer.framework.ui.preview.previewPainter

@Composable
internal fun BookThumbnailImage(thumbnail: FileThumbnail, modifier: Modifier = Modifier) {
    SubcomposeAsyncImage(
        model = thumbnail,
        contentDescription = null,
        loading = {
            CircularProgressIndicator(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center)
            )
        },
        error = {
            if (LocalInspectionMode.current) {
                Image(
                    painter = previewPainter(),
                    contentDescription = null,
                    contentScale = contentScale
                )
            } else {
                Icon(
                    imageVector = ComicIcons.BrokenImage,
                    contentDescription = null,
                    tint = LocalComponentColors.current.containerColor,
                    modifier = Modifier
                        .wrapContentSize()
                        .sizeIn(minHeight = 48.dp, minWidth = 48.dp)
                        .align(Alignment.Center)
                )
            }
        },
        contentScale = ContentScale.Fit,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(LocalComponentColors.current.contentColor.complementary())
    )
}

fun Color.complementary(): Color {
    val maxmin = maxOf(red, green, blue) + minOf(red, green, blue)
    return copy(red = maxmin - red, green = maxmin - green, blue = maxmin - blue)
}
