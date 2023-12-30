package com.sorrowblue.comicviewer.feature.settings.display

import android.os.Parcelable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.display.section.AppearanceDialog
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import kotlinx.parcelize.Parcelize

@Composable
internal fun SettingsDisplayRoute(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    state: DisplaySettingsScreenState = rememberDisplaySettingsScreenState(),
) {
    SettingsDisplayScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onRestoreOnLaunchChange = state::onRestoreOnLaunchChange,
        onDarkModeClick = state::onDarkModeClick,
        contentPadding = contentPadding
    )

    if (state.appearanceDialogController.isShow) {
        AppearanceDialog(
            onDismissRequest = state::onAppearanceDismissRequest,
            currentDarkMode = state.uiState.darkMode,
            onDarkModeChange = state::onDarkModeChange
        )
    }
}

@Parcelize
internal data class SettingsDisplayScreenUiState(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val restoreOnLaunch: Boolean = false,
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsDisplayScreen(
    uiState: SettingsDisplayScreenUiState,
    onBackClick: () -> Unit,
    onRestoreOnLaunchChange: (Boolean) -> Unit,
    onDarkModeClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    SettingsDetailPane(
        title = {
            Text(text = stringResource(id = R.string.settings_display_title))
        },
        onBackClick = onBackClick,
        contentPadding = contentPadding
    ) {
        Setting(
            title = R.string.settings_display_label_appearance,
            summary = uiState.darkMode.label,
            icon = ComicIcons.DarkMode,
            onClick = onDarkModeClick
        )
        SwitchSetting(
            title = R.string.settings_display_label_show_last_folder,
            summary = R.string.settings_display_desc_show_last_folder,
            checked = uiState.restoreOnLaunch,
            onCheckedChange = onRestoreOnLaunchChange
        )
    }
}

internal val DarkMode.label
    get() = when (this) {
        DarkMode.DEVICE -> R.string.settings_display_label_system_default
        DarkMode.DARK -> R.string.settings_display_label_dark_mode
        DarkMode.LIGHT -> R.string.settings_display_label_light_mode
    }
