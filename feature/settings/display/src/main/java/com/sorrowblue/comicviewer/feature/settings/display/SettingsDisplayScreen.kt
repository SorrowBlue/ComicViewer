package com.sorrowblue.comicviewer.feature.settings.display

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.display.section.AppearanceDialog
import com.sorrowblue.comicviewer.feature.settings.display.section.AppearanceDialogController
import com.sorrowblue.comicviewer.feature.settings.display.section.rememberAppearanceDialogController
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SettingsDisplayScreenUiState(
    val darkMode: DarkMode = DarkMode.DEVICE,
    val restoreOnLaunch: Boolean = false,
) : Parcelable

@Composable
internal fun SettingsDisplayRoute(
    onBackClick: () -> Unit,
) {
    val state = rememberDisplaySettingsScreenState()
    SettingsDisplayScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onRestoreOnLaunchChange = state::onRestoreOnLaunchChange,
        onDarkModeClick = state::onDarkModeClick,
    )

    if (state.appearanceDialogController.isShow) {
        AppearanceDialog(
            onDismissRequest = state::onAppearanceDismissRequest,
            currentDarkMode = state.uiState.darkMode,
            onDarkModeChange = state::onDarkModeChange
        )
    }
}

@Stable
internal class DisplaySettingsScreenState(
    initUiState: SettingsDisplayScreenUiState = SettingsDisplayScreenUiState(),
    val appearanceDialogController: AppearanceDialogController,
    scope: CoroutineScope,
    private val viewModel: SettingsDisplayViewModel,
) {

    var uiState by mutableStateOf(initUiState)

    init {
        scope.launch {
            viewModel.displaySettings.collectLatest {
                uiState = uiState.copy(darkMode = it.darkMode, restoreOnLaunch = it.restoreOnLaunch)
            }
        }
    }

    fun onDarkModeChange(darkMode: DarkMode) {
        viewModel.updateDarkMode(darkMode)
        appearanceDialogController.dismiss()
    }

    fun onRestoreOnLaunchChange(value: Boolean) {
        viewModel.onRestoreOnLaunchChange(value)
    }

    fun onDarkModeClick() {
        appearanceDialogController.show(uiState.darkMode)
    }

    fun onAppearanceDismissRequest() {
        appearanceDialogController.dismiss()
    }
}

@Composable
internal fun rememberDisplaySettingsScreenState(
    appearanceDialogController: AppearanceDialogController = rememberAppearanceDialogController(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SettingsDisplayViewModel = hiltViewModel(),
): DisplaySettingsScreenState = rememberSaveable(
    saver = Saver(
        save = { it.uiState },
        restore = { uiState ->
            DisplaySettingsScreenState(
                initUiState = uiState,
                appearanceDialogController = appearanceDialogController,
                scope = scope,
                viewModel = viewModel
            )
        }
    )
) {
    DisplaySettingsScreenState(
        appearanceDialogController = appearanceDialogController,
        scope = scope,
        viewModel = viewModel
    )
}

@Composable
private fun SettingsDisplayScreen(
    uiState: SettingsDisplayScreenUiState = SettingsDisplayScreenUiState(),
    onBackClick: () -> Unit = {},
    onRestoreOnLaunchChange: (Boolean) -> Unit = {},
    onDarkModeClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.settings_display_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsColumn(contentPadding = contentPadding) {
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
}

internal val DarkMode.label
    get() = when (this) {
        DarkMode.DEVICE -> R.string.settings_display_label_system_default
        DarkMode.DARK -> R.string.settings_display_label_dark_mode
        DarkMode.LIGHT -> R.string.settings_display_label_light_mode
    }
