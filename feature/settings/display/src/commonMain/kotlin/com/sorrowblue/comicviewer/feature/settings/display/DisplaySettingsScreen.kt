package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.settings.display.generated.resources.Res
import comicviewer.feature.settings.display.generated.resources.settings_display_desc_show_last_folder
import comicviewer.feature.settings.display.generated.resources.settings_display_label_appearance
import comicviewer.feature.settings.display.generated.resources.settings_display_label_dark_mode
import comicviewer.feature.settings.display.generated.resources.settings_display_label_light_mode
import comicviewer.feature.settings.display.generated.resources.settings_display_label_show_last_folder
import comicviewer.feature.settings.display.generated.resources.settings_display_label_system_default
import comicviewer.feature.settings.display.generated.resources.settings_display_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

internal interface DisplaySettingsScreenNavigator : SettingsDetailNavigator {
    fun navigateToDarkMode()
}

@Serializable
data object DisplaySettings

@Destination<DisplaySettings>
@Composable
internal fun DisplaySettingsScreen(
    navigator: DisplaySettingsScreenNavigator = koinInject(),
    state: DisplaySettingsScreenState = rememberDisplaySettingsScreenState(),
) {
    DisplaySettingsScreen(
        uiState = state.uiState,
        onBackClick = navigator::navigateBack,
        onRestoreOnLaunchChange = state::onRestoreOnLaunchChange,
        onDarkModeClick = navigator::navigateToDarkMode,
    )
}

internal data class SettingsDisplayScreenUiState(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val restoreOnLaunch: Boolean = false,
)

@Composable
private fun DisplaySettingsScreen(
    uiState: SettingsDisplayScreenUiState,
    onBackClick: () -> Unit,
    onRestoreOnLaunchChange: (Boolean) -> Unit,
    onDarkModeClick: () -> Unit,
) {
    SettingsDetailPane(
        title = {
            Text(text = stringResource(Res.string.settings_display_title))
        },
        onBackClick = onBackClick,
    ) {
        Setting(
            title = Res.string.settings_display_label_appearance,
            summary = uiState.darkMode.label,
            icon = ComicIcons.DarkMode,
            onClick = onDarkModeClick
        )
        SwitchSetting(
            title = Res.string.settings_display_label_show_last_folder,
            summary = Res.string.settings_display_desc_show_last_folder,
            checked = uiState.restoreOnLaunch,
            onCheckedChange = onRestoreOnLaunchChange
        )
    }
}

internal val DarkMode.label
    get() = when (this) {
        DarkMode.DEVICE -> Res.string.settings_display_label_system_default
        DarkMode.DARK -> Res.string.settings_display_label_dark_mode
        DarkMode.LIGHT -> Res.string.settings_display_label_light_mode
    }
