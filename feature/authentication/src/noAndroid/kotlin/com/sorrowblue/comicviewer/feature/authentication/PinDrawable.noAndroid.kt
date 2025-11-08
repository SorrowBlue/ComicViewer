package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.authentication.generated.resources.Res
import comicviewer.feature.authentication.generated.resources.authentication_avd_favorite_circle
import org.jetbrains.compose.resources.vectorResource

@Composable
internal actual fun PinDrawable(
    index: Int,
    modifier: Modifier,
    animate: Boolean,
    enabled: Boolean,
) {
    Image(
        imageVector = vectorResource(Res.drawable.authentication_avd_favorite_circle),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.graphicsLayer(clip = false),
        colorFilter = ColorFilter.tint(
            if (enabled) {
                ComicTheme.colorScheme.onSurfaceVariant
            } else {
                ComicTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            },
        ),
    )
}
