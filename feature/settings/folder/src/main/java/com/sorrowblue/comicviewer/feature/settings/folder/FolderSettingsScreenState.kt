package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.ramcosta.composedestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Stable
internal interface FolderSettingsScreenState {
    val uiState: FolderSettingsScreenUiState
    fun onChangeOpenImageFolder(value: Boolean)
    fun onSavedThumbnailChange(value: Boolean)
    fun onFontSizeChange(size: Int)
    fun onThumbnailQualityChange(value: Int)
    fun onImageFormatChange(navResult: NavResult<ImageFormat>)
    fun onFileSortChange(navResult: NavResult<SortType>)
    fun onImageScaleChange(navResult: NavResult<ImageScale>)
    fun onShowHiddenFilesChange(value: Boolean)
    fun onShowThumbnailsChange(value: Boolean)
    fun onShowFilesExtensionChange(value: Boolean)
    fun onImageFilterChange(navResult: NavResult<ImageFilterQuality>)
    fun onFolderThumbnailOrder(navResult: NavResult<FolderThumbnailOrder>)
}

@Composable
internal fun rememberFolderSettingsScreenState(
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FolderSettingsViewModel = koinViewModel(),
): FolderSettingsScreenState = remember {
    FolderSettingsScreenStateImpl(
        scope = scope,
        manageFolderSettingsUseCase = viewModel.manageFolderSettingsUseCase,
        manageFolderDisplaySettingsUseCase = viewModel.manageFolderDisplaySettingsUseCase
    )
}

private class FolderSettingsScreenStateImpl(
    private val scope: CoroutineScope,
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : FolderSettingsScreenState {
    override var uiState: FolderSettingsScreenUiState by mutableStateOf(FolderSettingsScreenUiState())
        private set

    init {
        manageFolderSettingsUseCase.settings.onEach {
            uiState = uiState.copy(isOpenImageFolder = it.resolveImageFolder)
        }.launchIn(scope)
        manageFolderDisplaySettingsUseCase.settings.onEach {
            uiState = uiState.copy(
                showHiddenFiles = it.showHiddenFiles,
                showFilesExtension = it.showFilesExtension,
                fileSort = it.sortType,
                showThumbnails = it.showThumbnails,
                imageScale = it.imageScale,
                imageFilterQuality = it.imageFilterQuality,
                imageFormat = it.imageFormat,
                thumbnailQuality = it.thumbnailQuality,
                isSavedThumbnail = it.isSavedThumbnail,
                fontSize = it.fontSize,
                folderThumbnailOrder = it.folderThumbnailOrder
            )
        }.launchIn(scope)
    }

    override fun onChangeOpenImageFolder(value: Boolean) {
        scope.launch {
            manageFolderSettingsUseCase.edit {
                it.copy(resolveImageFolder = value)
            }
        }
    }

    override fun onShowFilesExtensionChange(value: Boolean) =
        editFolderDisplaySettings { it.copy(showFilesExtension = value) }

    override fun onShowHiddenFilesChange(value: Boolean) =
        editFolderDisplaySettings { it.copy(showHiddenFiles = value) }

    override fun onShowThumbnailsChange(value: Boolean) =
        editFolderDisplaySettings { it.copy(showThumbnails = value) }

    override fun onSavedThumbnailChange(value: Boolean) =
        editFolderDisplaySettings { it.copy(isSavedThumbnail = value) }

    override fun onFontSizeChange(size: Int) =
        editFolderDisplaySettings { it.copy(fontSize = size) }

    override fun onThumbnailQualityChange(value: Int) =
        editFolderDisplaySettings { it.copy(thumbnailQuality = value) }

    override fun onImageFormatChange(navResult: NavResult<ImageFormat>) =
        onNavResult(navResult) { settings, value -> settings.copy(imageFormat = value) }

    override fun onFileSortChange(navResult: NavResult<SortType>) =
        onNavResult(navResult) { settings, value -> settings.copy(sortType = value) }

    override fun onImageScaleChange(navResult: NavResult<ImageScale>) =
        onNavResult(navResult) { settings, value -> settings.copy(imageScale = value) }

    override fun onImageFilterChange(navResult: NavResult<ImageFilterQuality>) =
        onNavResult(navResult) { settings, value -> settings.copy(imageFilterQuality = value) }

    override fun onFolderThumbnailOrder(navResult: NavResult<FolderThumbnailOrder>) =
        onNavResult(navResult) { settings, value -> settings.copy(folderThumbnailOrder = value) }

    private fun editFolderDisplaySettings(onEdit: (FolderDisplaySettings) -> FolderDisplaySettings) {
        scope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                onEdit(it)
            }
        }
    }

    private fun <T> onNavResult(
        navResult: NavResult<T>,
        apply: (FolderDisplaySettings, T) -> FolderDisplaySettings,
    ) {
        when (navResult) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                scope.launch {
                    manageFolderDisplaySettingsUseCase.edit {
                        apply(it, navResult.value)
                    }
                }
            }
        }
    }
}
