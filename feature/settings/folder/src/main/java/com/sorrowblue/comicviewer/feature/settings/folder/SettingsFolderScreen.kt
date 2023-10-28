package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.feature.settings.common.SwitchSetting
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal data class SettingsFolderScreenUiState(
    val isOpenImageFolder: Boolean = false,
    val isThumbnailEnabled: Boolean = false,
)

@Composable
internal fun SettingsFolderRoute(
    onBackClick: () -> Unit,
    onExtensionClick: () -> Unit,
    state: SettingsFolderScreenState = rememberSettingsFolderScreenState(),
) {
    val uiState = state.uiState
    SettingsFolderScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onExtensionClick = onExtensionClick,
        onChangeOpenImageFolder = state::onChangeOpenImageFolder,
        onChangeThumbnailEnabled = state::onChangeThumbnailEnabled,
        onDeleteThumbnailClick = state::onDeleteThumbnailClick,
    )
}

@Stable
internal class SettingsFolderScreenState(
    scope: CoroutineScope,
    private val viewModel: SettingsFolderViewModel,
) {
    var uiState: SettingsFolderScreenUiState by mutableStateOf(SettingsFolderScreenUiState())
        private set

    init {
        viewModel.settings.onEach {
            uiState = uiState.copy(
                isOpenImageFolder = it.resolveImageFolder,
                isThumbnailEnabled = it.showPreview
            )
        }.launchIn(scope)
    }

    fun onChangeOpenImageFolder(value: Boolean) {
        viewModel.updateResolveImageFolder(value)
    }

    fun onChangeThumbnailEnabled(value: Boolean) {
        viewModel.updateShowPreview(value)
    }

    fun onDeleteThumbnailClick() {
        viewModel.deleteThumbnail()
    }
}

@Composable
internal fun rememberSettingsFolderScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: SettingsFolderViewModel = hiltViewModel(),
) = remember {
    SettingsFolderScreenState(scope = scope, viewModel = viewModel)
}

@Composable
private fun SettingsFolderScreen(
    uiState: SettingsFolderScreenUiState = SettingsFolderScreenUiState(),
    onBackClick: () -> Unit = {},
    onExtensionClick: () -> Unit = {},
    onChangeOpenImageFolder: (Boolean) -> Unit = {},
    onChangeThumbnailEnabled: (Boolean) -> Unit = {},
    onDeleteThumbnailClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.settings_folder_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SettingsColumn(contentPadding = contentPadding) {
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
                    onCheckedChange = onChangeThumbnailEnabled
                )
                Setting(
                    title = R.string.settings_folder_label_delete_thumbnail,
                    onClick = onDeleteThumbnailClick
                )
            }
        }
    }
}
