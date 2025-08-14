package com.sorrowblue.comicviewer.domain.model.settings.folder

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.serialization.Serializable

/**
 * Represents the display settings for folders.
 *
 * @property fileListDisplay The display type for files and folders.
 * @property isSavedThumbnail Whether to save thumbnails.
 * @property gridColumnSize The column size for grid display.
 * @property sortType The default sort type.
 * @property showHiddenFiles Whether to show hidden files.
 * @property showFilesExtension Whether to show file extensions.
 * @property showThumbnails Whether to show thumbnails.
 * @property fontSize The font size for display.
 * @property thumbnailQuality The quality of thumbnails.
 * @property imageFormat The image format for thumbnails.
 * @property imageScale The scale type for images.
 * @property imageFilterQuality The filter quality for images.
 * @property folderThumbnailOrder The order for folder thumbnails.
 * @property folderScopeOnlyList List of folder-specific settings.
 */
@Serializable
data class FolderDisplaySettings(
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    val sortType: SortType = FolderDisplaySettingsDefaults.sortType,
    val folderScopeOnlyList: List<FolderScopeOnly> = emptyList(),
    val showHiddenFiles: Boolean = FolderDisplaySettingsDefaults.isDisplayHiddenFile,
    val showFilesExtension: Boolean = FolderDisplaySettingsDefaults.isDisplayFileExtension,
    val showThumbnails: Boolean = FolderDisplaySettingsDefaults.isDisplayThumbnail,
    val isSavedThumbnail: Boolean = FolderDisplaySettingsDefaults.isSavedThumbnail,
    val fontSize: Int = FolderDisplaySettingsDefaults.fontSize,
    val thumbnailQuality: Int = FolderDisplaySettingsDefaults.thumbnailQuality,
    val imageFormat: ImageFormat = FolderDisplaySettingsDefaults.imageFormat,
    val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
    val folderThumbnailOrder: FolderThumbnailOrder = FolderDisplaySettingsDefaults.folderThumbnailOrder,
) {

    /**
     * Returns the current sort type for the specified bookshelf and path.
     *
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return The sort type for the folder, or the default sort type if not
     *    set.
     */
    fun currentSortType(bookshelfId: BookshelfId, path: String): SortType {
        return folderScopeOnlyList.find { it.bookshelfId == bookshelfId && it.path == path }?.sortType
            ?: sortType
    }
}

/** Default values for [FolderDisplaySettings]. */
object FolderDisplaySettingsDefaults {

    /** Default sort type. */
    val sortType = SortType.Name(true)

    /** Default file list display type. */
    val fileListDisplay = FileListDisplay.Grid

    /** Default grid column size. */
    val gridColumnSize = GridColumnSize.Medium

    /** Default font size. */
    const val fontSize = 16

    /** Whether to display file extensions by default. */
    const val isDisplayFileExtension = true

    /** Whether to display hidden files by default. */
    const val isDisplayHiddenFile = false

    /** Whether to display thumbnails by default. */
    const val isDisplayThumbnail = true

    /** Whether to save thumbnails by default. */
    const val isSavedThumbnail = true

    /** Default image format. */
    val imageFormat = ImageFormat.WEBP

    /** Default thumbnail quality. */
    const val thumbnailQuality = 75

    /** Default image scale type. */
    val imageScale = ImageScale.Fit

    /** Default image filter quality. */
    val imageFilterQuality = ImageFilterQuality.Medium

    /** Default folder thumbnail order. */
    val folderThumbnailOrder = FolderThumbnailOrder.NAME
}

/**
 * Represents folder-specific display settings.
 *
 * @property bookshelfId The ID of the bookshelf.
 * @property path The folder path.
 * @property sortType The sort type for the folder.
 */
@Serializable
data class FolderScopeOnly(
    val bookshelfId: BookshelfId,
    val path: String,
    val sortType: SortType,
)
