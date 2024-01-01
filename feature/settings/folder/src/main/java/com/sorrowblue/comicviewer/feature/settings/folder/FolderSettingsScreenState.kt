package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Stable
internal interface FolderSettingsScreenState {
    val uiState: FolderSettingsScreenUiState
    fun onChangeOpenImageFolder(value: Boolean)
    fun onChangeThumbnailEnabled(value: Boolean)
    fun onDeleteThumbnailClick()
}

@Composable
internal fun rememberFolderSettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FolderSettingsViewModel = hiltViewModel(),
): FolderSettingsScreenState = remember {
    FolderSettingsScreenStateImpl(scope = scope, viewModel = viewModel)
}

private class FolderSettingsScreenStateImpl(
    scope: CoroutineScope,
    private val viewModel: FolderSettingsViewModel,
) : FolderSettingsScreenState {
    override var uiState: FolderSettingsScreenUiState by mutableStateOf(FolderSettingsScreenUiState())
        private set

    init {
        viewModel.settings.onEach {
            uiState = uiState.copy(
                isOpenImageFolder = it.resolveImageFolder,
                isThumbnailEnabled = it.showPreview
            )
        }.launchIn(scope)
    }

    override fun onChangeOpenImageFolder(value: Boolean) {
        viewModel.updateResolveImageFolder(value)
    }

    override fun onChangeThumbnailEnabled(value: Boolean) {
        viewModel.updateShowPreview(value)
    }

    override fun onDeleteThumbnailClick() {
        viewModel.deleteThumbnail()
    }
}
