package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import io.github.takahirom.rin.rememberRetained
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@Composable
internal actual fun colorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme {
    val context = LocalPlatformContext.current
    val themeContext =
        rememberRetained { context.require<ThemeContext.Factory>().createThemeContext() }

    var darkMode by remember {
        mutableStateOf(
            runBlocking { themeContext.manageDisplaySettingsUseCase.settings.first().darkMode },
        )
    }
    LaunchedEffect(Unit) {
        themeContext.manageDisplaySettingsUseCase.settings.onEach {
            darkMode = it.darkMode
        }.launchIn(this)
    }
    return when (darkMode) {
        DarkMode.DEVICE -> when {
            darkTheme -> darkScheme
            else -> lightScheme
        }

        DarkMode.DARK -> darkScheme

        DarkMode.LIGHT -> lightScheme
    }
}
