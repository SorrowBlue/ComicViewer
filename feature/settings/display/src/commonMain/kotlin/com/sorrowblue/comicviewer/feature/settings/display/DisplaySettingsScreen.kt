package com.sorrowblue.comicviewer.feature.settings.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.common.Setting
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
import org.jetbrains.compose.resources.stringResource

internal data class SettingsDisplayScreenUiState(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val restoreOnLaunch: Boolean = false,
)

@Composable
internal fun DisplaySettingsScreen(
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
        modifier = Modifier.testTag("DisplaySettingsRoot"),
    ) {
        Setting(
            title = Res.string.settings_display_label_appearance,
            summary = uiState.darkMode.label,
            icon = ComicIcons.DarkMode,
            onClick = onDarkModeClick,
        )
        SwitchSetting(
            title = Res.string.settings_display_label_show_last_folder,
            summary = Res.string.settings_display_desc_show_last_folder,
            checked = uiState.restoreOnLaunch,
            onCheckedChange = onRestoreOnLaunchChange,
        )
    }
}

internal val DarkMode.label
    get() = when (this) {
        DarkMode.DEVICE -> Res.string.settings_display_label_system_default
        DarkMode.DARK -> Res.string.settings_display_label_dark_mode
        DarkMode.LIGHT -> Res.string.settings_display_label_light_mode
    }
