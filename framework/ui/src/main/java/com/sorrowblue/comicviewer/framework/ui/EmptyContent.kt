package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberWindowAdaptiveInfo

@Composable
fun EmptyContent(
    imageVector: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) {
    val windowAdaptiveInfo by rememberWindowAdaptiveInfo()
    if (windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = imageVector,
                contentDescription = text,
                modifier = Modifier
                    .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                    .fillMaxSize(0.5f)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        Surface(
            modifier = modifier,
            shape = ComicTheme.shapes.large
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = imageVector,
                    contentDescription = null,
                    modifier = Modifier
                        .sizeIn(maxWidth = 300.dp, maxHeight = 300.dp)
                        .fillMaxWidth(0.8f)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
