package com.sorrowblue.comicviewer.framework.compose

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.themeadapter.material3.Mdc3Theme

private val lightColorScheme = lightColorScheme()
private val darkColorScheme = darkColorScheme()

@Composable
fun AppMaterialTheme(
    readXmlTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    if (readXmlTheme) {
        val darkTheme = isSystemInDarkTheme()
        val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val colors = when {
            dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
            dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
            darkTheme -> darkColorScheme
            else -> lightColorScheme
        }
        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    } else {
        Mdc3Theme(content = content)
    }
}
