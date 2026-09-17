/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.feature.settings.nav.DisplaySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.ExtensionSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.FolderSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.InfoSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.SecuritySettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.nav.ViewerSettingsNavKey
import com.sorrowblue.comicviewer.feature.settings.navigation.InAppLanguagePickerNavKey
import com.sorrowblue.comicviewer.feature.settings.utils.AppLocaleSettingsLauncher
import com.sorrowblue.comicviewer.feature.settings.utils.rememberAppLocaleSettingsLauncher
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator

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
    SideEffect(navigator.backStack.lastOrNull()) {
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
