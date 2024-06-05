package com.sorrowblue.comicviewer.app.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun BuildTypeStatusBar(buildType: String) {
    if (buildType != "release") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                )
        ) {
            Text(
                text = buildType,
                color = ComicTheme.colorScheme.tertiary,
                style = ComicTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
