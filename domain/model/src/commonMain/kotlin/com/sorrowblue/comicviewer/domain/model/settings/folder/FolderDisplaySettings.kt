/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings.folder

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

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
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FolderDisplaySettings(
    @ProtoNumber(
        1,
    ) val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    @ProtoNumber(
        2,
    ) val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    @ProtoNumber(3) val sortType: SortType = FolderDisplaySettingsDefaults.sortType,
    @ProtoNumber(4) val folderScopeOnlyList: List<FolderScopeOnly> = emptyList(),
    @ProtoNumber(5) val showHiddenFiles: Boolean = FolderDisplaySettingsDefaults.DisplayHiddenFile,
    @ProtoNumber(
        6,
    ) val showFilesExtension: Boolean = FolderDisplaySettingsDefaults.DisplayFileExtension,
    @ProtoNumber(7) val showThumbnails: Boolean = FolderDisplaySettingsDefaults.DisplayThumbnail,
    @ProtoNumber(8) val isSavedThumbnail: Boolean = FolderDisplaySettingsDefaults.SavedThumbnail,
    @ProtoNumber(9) val fontSize: Int = FolderDisplaySettingsDefaults.FontSize,
    @ProtoNumber(10) val thumbnailQuality: Int = FolderDisplaySettingsDefaults.ThumbnailQuality,
    @ProtoNumber(11) val imageFormat: ImageFormat = FolderDisplaySettingsDefaults.imageFormat,
    @ProtoNumber(12) val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    @ProtoNumber(
        13,
    ) val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
    @ProtoNumber(14) val folderThumbnailOrder: FolderThumbnailOrder =
        FolderDisplaySettingsDefaults.folderThumbnailOrder,
) {
    /**
     * Returns the current sort type for the specified bookshelf and path.
     *
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return The sort type for the folder, or the default sort type if not
     *    set.
     */
    fun currentSortType(bookshelfId: BookshelfId, path: String): SortType = folderScopeOnlyList
        .find {
            it.bookshelfId == bookshelfId && it.path == path
        }?.sortType
        ?: sortType
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
    const val FontSize = 16

    /** Whether to display file extensions by default. */
    const val DisplayFileExtension = true

    /** Whether to display hidden files by default. */
    const val DisplayHiddenFile = false

    /** Whether to display thumbnails by default. */
    const val DisplayThumbnail = true

    /** Whether to save thumbnails by default. */
    const val SavedThumbnail = true

    /** Default image format. */
    val imageFormat = ImageFormat.WEBP

    /** Default thumbnail quality. */
    const val ThumbnailQuality = 75

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
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FolderScopeOnly(
    @ProtoNumber(1) val bookshelfId: BookshelfId,
    @ProtoNumber(2) val path: String,
    @ProtoNumber(3) val sortType: SortType,
)
