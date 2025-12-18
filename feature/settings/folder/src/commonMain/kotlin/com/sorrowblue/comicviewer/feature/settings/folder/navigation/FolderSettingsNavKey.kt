package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFilterQuality
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageFormat
import com.sorrowblue.comicviewer.domain.model.settings.folder.ImageScale
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data object FolderSettingsNavKey : ScreenKey

@Serializable
internal data class FolderThumbnailOrderNavKey(val folderThumbnailOrder: FolderThumbnailOrder) :
    ScreenKey

@Serializable
internal data class ImageFilterQualityNavKey(val imageFilterQuality: ImageFilterQuality) : ScreenKey

@Serializable
internal data class ImageFormatNavKey(val imageFormat: ImageFormat) : ScreenKey

@Serializable
internal data class ImageScaleNavKey(val imageScale: ImageScale) : ScreenKey

@Serializable
internal data class SortTypeNavKey(val sortType: SortType) : ScreenKey
