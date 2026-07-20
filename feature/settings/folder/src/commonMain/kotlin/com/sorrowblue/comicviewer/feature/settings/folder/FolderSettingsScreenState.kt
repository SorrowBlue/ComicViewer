/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn

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
internal fun rememberFolderSettingsScreenState(
    viewModel: FolderSettingsViewModel = metroViewModel(),
): FolderSettingsScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        FolderSettingsScreenStateImpl(
            coroutineScope = coroutineScope,
            folderSettingsFlow = viewModel.folderSettingsFlow,
            folderDisplaySettingsFlow = viewModel.folderDisplaySettingsFlow,
            updateFolderSettings = { viewModel.updateFolderSettings(it) },
            updateFolderDisplaySettings = { viewModel.updateFolderDisplaySettings(it) }
        )
    }
}

private class FolderSettingsScreenStateImpl(
    coroutineScope: CoroutineScope,
    folderSettingsFlow: SharedFlow<FolderSettings>,
    folderDisplaySettingsFlow: SharedFlow<FolderDisplaySettings>,
    private val updateFolderSettings: ((FolderSettings) -> FolderSettings) -> Unit,
    private val updateFolderDisplaySettings: ((FolderDisplaySettings) -> FolderDisplaySettings) -> Unit,
) : FolderSettingsScreenState {
    override var uiState: FolderSettingsScreenUiState by mutableStateOf(
        FolderSettingsScreenUiState(),
    )
        private set

    init {
        combine(
            folderSettingsFlow,
            folderDisplaySettingsFlow
        ) { folderSettings, folderDisplaySettings ->
            uiState = uiState.copy(
                isOpenImageFolder = folderSettings.resolveImageFolder,
                showHiddenFiles = folderDisplaySettings.showHiddenFiles,
                showFilesExtension = folderDisplaySettings.showFilesExtension,
                fileSort = folderDisplaySettings.sortType,
                showThumbnails = folderDisplaySettings.showThumbnails,
                imageScale = folderDisplaySettings.imageScale,
                imageFilterQuality = folderDisplaySettings.imageFilterQuality,
                imageFormat = folderDisplaySettings.imageFormat,
                thumbnailQuality = folderDisplaySettings.thumbnailQuality,
                isSavedThumbnail = folderDisplaySettings.isSavedThumbnail,
                fontSize = folderDisplaySettings.fontSize,
                folderThumbnailOrder = folderDisplaySettings.folderThumbnailOrder,
            )
        }.launchIn(coroutineScope)
    }

    override fun onChangeOpenImageFolder(value: Boolean) {
        updateFolderSettings { it.copy(resolveImageFolder = value) }
    }

    override fun onShowFilesExtensionChange(value: Boolean) =
        updateFolderDisplaySettings { it.copy(showFilesExtension = value) }

    override fun onShowHiddenFilesChange(value: Boolean) =
        updateFolderDisplaySettings { it.copy(showHiddenFiles = value) }

    override fun onShowThumbnailsChange(value: Boolean) =
        updateFolderDisplaySettings { it.copy(showThumbnails = value) }

    override fun onSavedThumbnailChange(value: Boolean) =
        updateFolderDisplaySettings { it.copy(isSavedThumbnail = value) }

    override fun onFontSizeChange(size: Int) =
        updateFolderDisplaySettings { it.copy(fontSize = size) }

    override fun onThumbnailQualityChange(value: Int) =
        updateFolderDisplaySettings { it.copy(thumbnailQuality = value) }

    override fun onImageFormatChange(value: ImageFormat) =
        updateFolderDisplaySettings { it.copy(imageFormat = value) }

    override fun onFileSortChange(value: SortType) =
        updateFolderDisplaySettings { it.copy(sortType = value) }

    override fun onImageScaleChange(value: ImageScale) =
        updateFolderDisplaySettings { it.copy(imageScale = value) }

    override fun onImageFilterQualityChange(value: ImageFilterQuality) =
        updateFolderDisplaySettings { it.copy(imageFilterQuality = value) }

    override fun onFolderThumbnailOrderChange(value: FolderThumbnailOrder) =
        updateFolderDisplaySettings { it.copy(folderThumbnailOrder = value) }
}
