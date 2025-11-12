package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

internal interface FolderSettingsScreenState {
    val uiState: FolderSettingsScreenUiState

    fun onChangeOpenImageFolder(value: Boolean)

    fun onSavedThumbnailChange(value: Boolean)

    fun onFontSizeChange(size: Int)

    fun onThumbnailQualityChange(value: Int)

    fun onImageFormatChange(value: ImageFormat)

    fun onFileSortChange(value: SortType)

    fun onImageScaleChange(value: ImageScale)

    fun onShowHiddenFilesChange(value: Boolean)

    fun onShowThumbnailsChange(value: Boolean)

    fun onShowFilesExtensionChange(value: Boolean)

    fun onImageFilterQualityChange(value: ImageFilterQuality)

    fun onFolderThumbnailOrderChange(value: FolderThumbnailOrder)
}

@Composable
context(context: FolderSettingsScreenContext)
internal fun rememberFolderSettingsScreenState(): FolderSettingsScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        FolderSettingsScreenStateImpl(
            manageFolderSettingsUseCase = context.manageFolderSettingsUseCase,
            manageFolderDisplaySettingsUseCase = context.manageFolderDisplaySettingsUseCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        this.coroutineScope = coroutineScope
    }
}

private class FolderSettingsScreenStateImpl(
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    var coroutineScope: CoroutineScope,
) : FolderSettingsScreenState {
    override var uiState: FolderSettingsScreenUiState by mutableStateOf(
        FolderSettingsScreenUiState(),
    )
        private set

    init {
        manageFolderSettingsUseCase.settings
            .onEach {
                uiState = uiState.copy(isOpenImageFolder = it.resolveImageFolder)
            }.launchIn(coroutineScope)
        manageFolderDisplaySettingsUseCase.settings
            .onEach {
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
                    folderThumbnailOrder = it.folderThumbnailOrder,
                )
            }.launchIn(coroutineScope)
    }

    override fun onChangeOpenImageFolder(value: Boolean) {
        coroutineScope.launch {
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

    override fun onImageFormatChange(value: ImageFormat) =
        editFolderDisplaySettings { it.copy(imageFormat = value) }

    override fun onFileSortChange(value: SortType) =
        editFolderDisplaySettings { it.copy(sortType = value) }

    override fun onImageScaleChange(value: ImageScale) =
        editFolderDisplaySettings { it.copy(imageScale = value) }

    override fun onImageFilterQualityChange(value: ImageFilterQuality) =
        editFolderDisplaySettings { it.copy(imageFilterQuality = value) }

    override fun onFolderThumbnailOrderChange(value: FolderThumbnailOrder) =
        editFolderDisplaySettings { it.copy(folderThumbnailOrder = value) }

    private fun editFolderDisplaySettings(
        onEdit: (FolderDisplaySettings) -> FolderDisplaySettings,
    ) {
        coroutineScope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                onEdit(it)
            }
        }
    }
}
