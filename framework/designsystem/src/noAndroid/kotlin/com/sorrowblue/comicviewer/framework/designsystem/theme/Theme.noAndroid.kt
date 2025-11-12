package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.platformGraph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Composable
internal actual fun colorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme {
    val context = LocalPlatformContext.current.platformGraph as ThemeContext
    return when (
        runBlocking {
            context.settingsUseCase.settings
                .first()
                .darkMode
        }
    ) {
        DarkMode.DEVICE -> when {
            darkTheme -> darkScheme
            else -> lightScheme
        }
        DarkMode.DARK -> darkScheme
        DarkMode.LIGHT -> lightScheme
    }
}

interface ThemeContext {
    val settingsUseCase: ManageDisplaySettingsUseCase
}
