package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun ActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Card(
            modifier = Modifier.height(80.dp).fillMaxWidth(),
            enabled = enabled,
            shape = ComicTheme.shapes.small,
            onClick = onClick,
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                icon()
                if (loading) {
                    RotatingBorderAnimation(
                        borderWidth = 4.dp,
                        modifier = Modifier.fillMaxSize(),
                        borderColor = ComicTheme.colorScheme.primary,
                        segmentLengthRatio = 0.4f,
                        durationMillis = 2000,
                        strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
        Spacer(Modifier.size(ComicTheme.dimension.padding))
        CompositionLocalProvider(LocalTextStyle provides ComicTheme.typography.labelSmall) {
            text()
        }
    }
}
