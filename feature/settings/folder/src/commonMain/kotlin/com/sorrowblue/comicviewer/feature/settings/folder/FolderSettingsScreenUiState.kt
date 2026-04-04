package com.sorrowblue.comicviewer.feature.settings.folder

import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType

internal data class FolderSettingsScreenUiState(
    val showHiddenFiles: Boolean = FolderDisplaySettingsDefaults.DisplayHiddenFile,
    val showFilesExtension: Boolean = FolderDisplaySettingsDefaults.DisplayFileExtension,
    val fileSort: SortType = FolderDisplaySettingsDefaults.sortType,
    val showThumbnails: Boolean = FolderDisplaySettingsDefaults.DisplayThumbnail,
    val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
    val imageFormat: ImageFormat = FolderDisplaySettingsDefaults.imageFormat,
    val thumbnailQuality: Int = FolderDisplaySettingsDefaults.ThumbnailQuality,
    val isSavedThumbnail: Boolean = FolderDisplaySettingsDefaults.SavedThumbnail,
    val isOpenImageFolder: Boolean = false,
    val fontSize: Int = FolderDisplaySettingsDefaults.FontSize,
    val folderThumbnailOrder: FolderThumbnailOrder = FolderDisplaySettingsDefaults.folderThumbnailOrder,
)
