package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.feature.settings.utils.AppLocaleSettingsLauncher
import com.sorrowblue.comicviewer.feature.settings.utils.rememberAppLocaleSettingsLauncher

internal interface SettingsScreenState {

    val uiState: SettingsScreenUiState

    fun onSettingsClick(item: SettingsItem, onSettingsClick: (SettingsItem) -> Unit)
}

@Composable
internal fun rememberSettingsScreenState(): SettingsScreenState {
    val appLocaleSettingsLauncher = rememberAppLocaleSettingsLauncher()
    return remember(appLocaleSettingsLauncher) {
        SettingsScreenStateImpl(
            appLocaleSettingsLauncher = appLocaleSettingsLauncher,
        )
    }
}

private class SettingsScreenStateImpl(
    private val appLocaleSettingsLauncher: AppLocaleSettingsLauncher,
) : SettingsScreenState {

    override val uiState by mutableStateOf(SettingsScreenUiState())

    override fun onSettingsClick(item: SettingsItem, onSettingsClick: (SettingsItem) -> Unit) {
        when (item) {
            SettingsItem.LANGUAGE -> appLocaleSettingsLauncher.launch {
                onSettingsClick(item)
            }

            else -> onSettingsClick(item)
        }
    }
}
