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
    fun currentSortType(bookshelfId: BookshelfId, path: String): SortType {
        val targetList = folderScopeOnlyList.filter { it.bookshelfId == bookshelfId }
        targetList.find { isSameFolderPath(it.path, path) }?.let {
            return if (it.useGlobal) sortType else it.sortType
        }
        return targetList
            .filter {
                !it.useGlobal &&
                    it.includeSubfolders &&
                    isSubfolderPath(parent = it.path, child = path)
            }
            .maxByOrNull { normalizeFolderPath(it.path).length }
            ?.sortType
            ?: sortType
    }

    /**
     * Returns the effective sort scope for the specified bookshelf and path.
     *
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return The [FolderSortScope] currently applied to the folder.
     */
    fun currentSortScope(bookshelfId: BookshelfId, path: String): FolderSortScope {
        val targetList = folderScopeOnlyList.filter { it.bookshelfId == bookshelfId }
        targetList.find { isSameFolderPath(it.path, path) }?.let {
            return when {
                it.useGlobal -> FolderSortScope.Global
                it.includeSubfolders -> FolderSortScope.Subfolders
                else -> FolderSortScope.FolderOnly
            }
        }
        val inheritedFromAncestor = targetList.any {
            !it.useGlobal &&
                it.includeSubfolders &&
                isSubfolderPath(parent = it.path, child = path)
        }
        return if (inheritedFromAncestor) {
            FolderSortScope.Subfolders
        } else {
            FolderSortScope.Global
        }
    }

    /**
     * Checks whether the specified folder has a folder-specific sort setting.
     *
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return True if a folder-specific setting exists, false otherwise.
     */
    fun isFolderScopeOnly(bookshelfId: BookshelfId, path: String): Boolean =
        currentSortScope(bookshelfId, path) != FolderSortScope.Global

    /**
     * Checks whether the specified folder has subfolder sort inheritance enabled.
     *
     * @param bookshelfId The ID of the bookshelf.
     * @param path The folder path.
     * @return True if subfolder sort inheritance is enabled, false otherwise.
     */
    fun isIncludeSubfolders(bookshelfId: BookshelfId, path: String): Boolean =
        currentSortScope(bookshelfId, path) == FolderSortScope.Subfolders
}

/**
 * Compares two folder paths for equality after normalizing path separators and trailing slashes.
 *
 * @param path1 The first folder path.
 * @param path2 The second folder path.
 * @return True if both paths refer to the same folder, false otherwise.
 */
fun isSameFolderPath(path1: String, path2: String): Boolean =
    normalizeFolderPath(path1) == normalizeFolderPath(path2)

/**
 * Checks whether [child] is a subfolder of [parent] after normalizing path separators and trailing slashes.
 *
 * @param parent The ancestor folder path.
 * @param child The candidate descendant folder path.
 * @return True if [child] is strictly inside [parent], false otherwise.
 */
fun isSubfolderPath(parent: String, child: String): Boolean {
    val normParent = normalizeFolderPath(parent)
    val normChild = normalizeFolderPath(child)
    if (normParent.isEmpty() || normChild.isEmpty() || normParent == normChild) return false
    return if (normParent.endsWith("/")) {
        normChild.startsWith(normParent)
    } else {
        normChild.startsWith("$normParent/")
    }
}

/**
 * Normalizes a folder path by unifying separators (`\`, `%2F`) to `/` and removing trailing slashes.
 *
 * @param path The raw folder path.
 * @return The normalized folder path.
 */
fun normalizeFolderPath(path: String): String {
    if (path.isEmpty()) return ""
    val normalized = path
        .replace("\\", "/")
        .replace("%2F", "/", ignoreCase = true)
    val trimmed = normalized.trimEnd('/')
    return when {
        trimmed.isEmpty() -> "/"
        trimmed.length == 2 && trimmed[1] == ':' && normalized.endsWith("/") -> "$trimmed/"
        else -> trimmed
    }
}

/**
 * Represents the application scope of folder sort settings.
 */
@Serializable
enum class FolderSortScope {
    /** Apply sort settings globally to all folders without custom settings. */
    Global,

    /** Apply sort settings only to the current folder. */
    FolderOnly,

    /** Apply sort settings to the current folder and all of its subfolders. */
    Subfolders,
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
 * @property includeSubfolders Whether to apply this sort type to subfolders.
 * @property useGlobal Whether this folder explicitly uses the global sort type
 *    even when an ancestor has [includeSubfolders] enabled.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FolderScopeOnly(
    @ProtoNumber(1) val bookshelfId: BookshelfId,
    @ProtoNumber(2) val path: String,
    @ProtoNumber(3) val sortType: SortType,
    @ProtoNumber(4) val includeSubfolders: Boolean = false,
    @ProtoNumber(5) val useGlobal: Boolean = false,
)
