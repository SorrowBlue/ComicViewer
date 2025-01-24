package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.runtime.Composable

@Composable
internal actual fun colorScheme(darkTheme: Boolean, dynamicColor: Boolean) = when {
    darkTheme -> darkScheme
    else -> lightScheme
}
