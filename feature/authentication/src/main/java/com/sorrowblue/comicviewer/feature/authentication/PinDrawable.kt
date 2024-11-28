package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
fun PinDrawable(
    index: Int,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    enabled: Boolean = true,
) {
    val resource = when (index % 4) {
        0 -> R.drawable.authentication_avd_favorite_circle
        1 -> R.drawable.authentication_avd_moon_circle
        2 -> R.drawable.authentication_avd_play_circle
        else -> R.drawable.authentication_avd_hexagon_circle
    }
    val image =
        AnimatedImageVector.animatedVectorResource(resource)
    var atEnd by remember { mutableStateOf(!animate) }
    Image(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.graphicsLayer(clip = false),
        colorFilter = ColorFilter.tint(
            if (enabled) {
                ComicTheme.colorScheme.onSurfaceVariant
            } else {
                ComicTheme.colorScheme.onSurface.copy(
                    alpha = 0.38f
                )
            }
        )
    )
    LaunchedEffect(Unit) {
        atEnd = true
    }
}
