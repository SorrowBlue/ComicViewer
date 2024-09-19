package com.sorrowblue.comicviewer.app.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
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
            val adaptiveInfo = currentWindowAdaptiveInfo()
            val windowSizeClass = adaptiveInfo.windowSizeClass
            val orientation =
                when (LocalConfiguration.current.orientation) {
                    Configuration.ORIENTATION_LANDSCAPE -> "LANDSCAPE"
                    Configuration.ORIENTATION_PORTRAIT -> "PORTRAIT"
                    Configuration.ORIENTATION_UNDEFINED -> "UNDEFINED"
                    else -> "UNKNOWN"
                }

            Text(
                text = "b:$buildType. w:${windowSizeClass.windowWidthSizeClass.toString2()} h:${windowSizeClass.windowHeightSizeClass.toString2()} o:$orientation",
                color = ComicTheme.colorScheme.tertiary,
                style = ComicTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private fun WindowWidthSizeClass.toString2(): String {
    return when (this) {
        WindowWidthSizeClass.COMPACT -> "COMPACT"
        WindowWidthSizeClass.MEDIUM -> "MEDIUM"
        WindowWidthSizeClass.EXPANDED -> "EXPANDED"
        else -> "UNKNOWN"
    }
}

private fun WindowHeightSizeClass.toString2(): String {
    return when (this) {
        WindowHeightSizeClass.COMPACT -> "COMPACT"
        WindowHeightSizeClass.MEDIUM -> "MEDIUM"
        WindowHeightSizeClass.EXPANDED -> "EXPANDED"
        else -> "UNKNOWN"
    }
}
