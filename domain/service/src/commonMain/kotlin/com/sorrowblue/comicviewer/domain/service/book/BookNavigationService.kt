/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.book

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.model.settings.folder.normalizeFolderPath
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Domain service that resolves sort types, extracts parent folder paths, and validates book models
 * when navigating to the previous or next book during comic reading.
 */
interface BookNavigationService {

    /**
     * Resolves the effective sort type applied to the parent folder during folder browsing.
     * Prioritizes folder-specific settings or inherited parent settings over the global setting.
     *
     * @param settings Folder display settings.
     * @param bookshelfId Bookshelf ID.
     * @param parentPath Parent folder path.
     * @return The resolved [SortType].
     */
    fun resolveFolderSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        parentPath: String,
    ): SortType

    /**
     * Resolves the effective sort type according to the collection type during collection browsing.
     * Returns the search condition's sort type for [SmartCollection] or the folder display setting's
     * sort type for [BasicCollection].
     *
     * @param collection Target collection.
     * @param settings Folder display settings.
     * @return The resolved [SortType].
     */
    fun resolveCollectionSortType(
        collection: Collection,
        settings: FolderDisplaySettings,
    ): SortType

    /**
     * Extracts the parent folder path from a file or folder path.
     *
     * @param path Target file or folder path.
     * @return The parent folder path, or an empty string if the path is at the root or has no parent.
     */
    fun extractParentPath(path: String): String

    /**
     * Validates whether the retrieved file is a valid [Book].
     *
     * @param file File to validate.
     * @return The [Book] instance if valid, or `null` otherwise.
     */
    fun validateBook(file: File?): Book?

    companion object {
        /**
         * Creates and returns an instance of [BookNavigationService].
         *
         * @return An instance of [BookNavigationService].
         */
        operator fun invoke(): BookNavigationService = BookNavigationServiceImpl()
    }
}

@ContributesBinding(AppScope::class)
@Inject
class BookNavigationServiceImpl : BookNavigationService {

    override fun resolveFolderSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        parentPath: String,
    ): SortType = settings.currentSortType(
        bookshelfId = bookshelfId,
        path = parentPath,
    )

    override fun resolveCollectionSortType(
        collection: Collection,
        settings: FolderDisplaySettings,
    ): SortType = when (collection) {
        is SmartCollection -> collection.searchCondition.sortType
        is BasicCollection -> settings.sortType
    }

    override fun extractParentPath(path: String): String {
        val trimmed = normalizeFolderPath(path).removeSuffix("/")
        val lastIndex = trimmed.lastIndexOf('/')
        return if (lastIndex > 0) {
            trimmed.substring(0, lastIndex)
        } else {
            ""
        }
    }

    override fun validateBook(file: File?): Book? = file as? Book
}
