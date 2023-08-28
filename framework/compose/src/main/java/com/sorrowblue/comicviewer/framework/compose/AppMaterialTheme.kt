package com.sorrowblue.comicviewer.framework.compose

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

private val lightColorScheme = lightColorScheme()
private val darkColorScheme = darkColorScheme()
private val localAppDimens = staticCompositionLocalOf {
    compactDimensions
}

object AppMaterialTheme {

    val dimens: Dimensions
        @Composable
        get() = localAppDimens.current
}

@Composable
fun AppMaterialTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    val configuration = LocalConfiguration.current
    val dimensions =
        when (configuration.screenWidthDp) {
            in 0..<600 -> compactDimensions
                in 600..<840 -> mediumDimensions
                else -> expandedDimensions
            }
        CompositionLocalProvider(localAppDimens provides remember { dimensions }) {
            MaterialTheme(
                colorScheme = colors,
                content = content
            )
        }
}
