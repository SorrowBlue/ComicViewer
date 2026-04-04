package com.sorrowblue.comicviewer.file.component

import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettingsDefaults
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import kotlinx.serialization.Serializable

@Serializable
data class FileLazyVerticalGridUiState(
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val columnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    val showThumbnails: Boolean = FolderDisplaySettingsDefaults.SavedThumbnail,
    val fontSize: Int = FolderDisplaySettingsDefaults.FontSize,
    val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
)
