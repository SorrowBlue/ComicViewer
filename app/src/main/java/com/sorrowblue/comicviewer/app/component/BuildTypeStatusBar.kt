package com.sorrowblue.comicviewer.app.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun BuildTypeStatusBar(buildType: String) {
    if (buildType != "release") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    (WindowInsets.statusBars.getTop(LocalDensity.current) / LocalDensity.current.density).dp
                )
                .background(ComicTheme.colorScheme.tertiaryContainer.copy(alpha = 0.25f))
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .padding(end = 80.dp)
        ) {
            Text(
                text = buildType,
                color = ComicTheme.colorScheme.onTertiaryContainer,
                style = ComicTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}
