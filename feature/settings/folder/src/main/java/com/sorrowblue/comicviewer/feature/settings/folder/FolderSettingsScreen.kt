package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraph
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraphTransitions

internal data class FolderSettingsScreenUiState(
    val isOpenImageFolder: Boolean = false,
    val isThumbnailEnabled: Boolean = false,
)

internal interface FolderSettingsScreenNavigator : SettingsDetailNavigator {

    fun navigateToExtension()
}

@Destination<FolderSettingsGraph>(
    start = true,
    style = FolderSettingsGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FolderSettingsScreen(
    contentPadding: PaddingValues,
    navigator: FolderSettingsScreenNavigator,
) {
    FolderSettingsScreen(
        contentPadding = contentPadding,
        onBackClick = navigator::navigateBack,
        onExtensionClick = navigator::navigateToExtension
    )
}

@Composable
private fun FolderSettingsScreen(
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onExtensionClick: () -> Unit,
    state: FolderSettingsScreenState = rememberFolderSettingsScreenState(),
) {
    val uiState = state.uiState
    FolderSettingsScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onExtensionClick = onExtensionClick,
        onChangeOpenImageFolder = state::onChangeOpenImageFolder,
        onChangeThumbnailEnable = state::onChangeThumbnailEnabled,
        onDeleteThumbnailClick = state::onDeleteThumbnailClick,
        contentPadding = contentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolderSettingsScreen(
    uiState: FolderSettingsScreenUiState,
    onBackClick: () -> Unit,
    onExtensionClick: () -> Unit,
    onChangeOpenImageFolder: (Boolean) -> Unit,
    onChangeThumbnailEnable: (Boolean) -> Unit,
    onDeleteThumbnailClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    SettingsDetailPane(
        title = {
            Text(text = stringResource(id = R.string.settings_folder_title))
        },
        onBackClick = onBackClick,
        contentPadding = contentPadding
    ) {
        Setting(title = R.string.settings_folder_title_extension, onClick = onExtensionClick)
        SwitchSetting(
            title = R.string.settings_folder_label_image_folder,
            summary = R.string.settings_folder_desc_image_folder,
            checked = uiState.isOpenImageFolder,
            onCheckedChange = onChangeOpenImageFolder
        )
        SettingsCategory(title = R.string.settings_folder_label_thumbnail) {
            SwitchSetting(
                title = R.string.settings_folder_label_show_thumbnail,
                checked = uiState.isThumbnailEnabled,
                onCheckedChange = onChangeThumbnailEnable
            )
            Setting(
                title = R.string.settings_folder_label_delete_thumbnail,
                onClick = onDeleteThumbnailClick
            )
        }
    }
}
