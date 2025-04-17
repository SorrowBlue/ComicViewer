package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.compose.koinInject

@Composable
internal actual fun colorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme {
    val settingsUseCase = koinInject<ManageDisplaySettingsUseCase>()
    return when (runBlocking { settingsUseCase.settings.first().darkMode }) {
        DarkMode.DEVICE -> when {
            darkTheme -> darkScheme
            else -> lightScheme
        }
        DarkMode.DARK -> darkScheme
        DarkMode.LIGHT -> lightScheme
    }
}
