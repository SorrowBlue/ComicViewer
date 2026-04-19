package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.extension.navigation.ExtensionSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.info.navigation.InfoSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.navigation.InAppLanguagePickerNavKey
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.utils.AppLocaleSettingsLauncher
import com.sorrowblue.comicviewer.feature.settings.utils.rememberAppLocaleSettingsLauncher
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.LocalNavigator
import logcat.logcat

internal interface SettingsScreenState {
    val uiState: SettingsScreenUiState

    fun onSettingsClick(item: SettingsItem, onSettingsClick: (SettingsItem) -> Unit)
}

@Composable
internal fun rememberSettingsScreenState(): SettingsScreenState {
    val appLocaleSettingsLauncher = rememberAppLocaleSettingsLauncher()
    val navigator = LocalNavigator.current
    val state = remember(appLocaleSettingsLauncher) {
        SettingsScreenStateImpl(appLocaleSettingsLauncher = appLocaleSettingsLauncher)
    }

    LaunchedEffect(navigator.backStack.lastOrNull()) {
        logcat(
            "Navigator",
        ) { "SettingsScreenState: #LaunchedEffect: ${navigator.backStack.lastOrNull()}" }
        when (navigator.backStack.lastOrNull()) {
            is DisplaySettingsNavKey -> SettingsItem.DISPLAY
            is FolderSettingsNavKey -> SettingsItem.FOLDER
            is ViewerSettingsNavKey -> SettingsItem.VIEWER
            is SecuritySettingsNavKey -> SettingsItem.SECURITY
            is InAppLanguagePickerNavKey -> SettingsItem.LANGUAGE
            is ExtensionSettingsNavKey -> SettingsItem.EXTENSION
            is InfoSettingsNavKey -> SettingsItem.HELP
            else -> null
        }?.let {
            state.uiState = state.uiState.copy(currentSettings = it)
        }
    }

    return state
}

private class SettingsScreenStateImpl(
    private val appLocaleSettingsLauncher: AppLocaleSettingsLauncher,
) : SettingsScreenState {
    override var uiState by mutableStateOf(SettingsScreenUiState())

    override fun onSettingsClick(item: SettingsItem, onSettingsClick: (SettingsItem) -> Unit) {
        uiState = uiState.copy(currentSettings = item)
        when (item) {
            SettingsItem.LANGUAGE -> appLocaleSettingsLauncher.launch {
                onSettingsClick(item)
            }

            else -> onSettingsClick(item)
        }
    }
}
