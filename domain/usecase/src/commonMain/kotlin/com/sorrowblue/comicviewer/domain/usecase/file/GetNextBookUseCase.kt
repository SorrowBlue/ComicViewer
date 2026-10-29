/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.service.book.BookNavigationService
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import logcat.logcat

/**
 * Resolves the next or previous [Book] relative to a given file path in a folder or collection.
 */
abstract class GetNextBookUseCase :
    OneShotUseCase<GetNextBookUseCase.Request, Book, GetNextBookUseCase.Error>() {

    /**
     * Request parameters for [GetNextBookUseCase].
     *
     * @property bookshelfId The bookshelf ID of the current book.
     * @property path The path of the current book.
     * @property location The browsing context (folder or collection).
     * @property isNext Whether to resolve the next book (`true`) or previous book (`false`).
     */
    data class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val location: Location,
        val isNext: Boolean,
    )

    /**
     * Represents the browsing context for book navigation.
     */
    sealed interface Location {
        /**
         * Navigating within a folder hierarchy.
         */
        data object Folder : Location

        /**
         * Navigating within a specific collection.
         *
         * @property collectionId The ID of the collection.
         */
        data class Collection(val collectionId: CollectionId) : Location
    }

    /**
     * Errors that can occur when resolving the next or previous book.
     */
    enum class Error : Resource.AppError {
        /**
         * Indicates that a system error occurred.
         */
        System,

        /**
         * Indicates that no adjacent book was found.
         */
        NotFound,
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetNextBookUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val fileRepository: FileRepository,
    private val collectionFileRepository: CollectionFileRepository,
    private val collectionRepository: CollectionRepository,
    private val bookNavigationService: BookNavigationService,
) : GetNextBookUseCase() {

    override suspend fun run(request: Request): Resource<Book, Error> = resolveBookResult {
        val settings = settingsRepository.folderDisplaySettings.first()
        when (val location = request.location) {
            is Location.Collection -> collection(
                request.isNext,
                location.collectionId,
                request.bookshelfId,
                request.path,
                settings,
            )

            Location.Folder -> folder(
                request.isNext,
                request.bookshelfId,
                request.path,
                settings,
            )
        }
    }

    private suspend fun folder(
        isNext: Boolean,
        bookshelfId: BookshelfId,
        path: String,
        settings: FolderDisplaySettings,
    ): File? {
        val currentFile = fileRepository.findBy(bookshelfId, path)
        val parentPath = currentFile?.parent ?: bookNavigationService.extractParentPath(path)
        val sortType = bookNavigationService.resolveFolderSortType(
            settings = settings,
            bookshelfId = bookshelfId,
            parentPath = parentPath,
        )

        val modelFlow = if (isNext) {
            fileRepository.nextFile(bookshelfId, path, sortType)
        } else {
            fileRepository.prevFile(bookshelfId, path, sortType)
        }
        return modelFlow.first()
    }

    private suspend fun collection(
        isNext: Boolean,
        collectionId: CollectionId,
        bookshelfId: BookshelfId,
        path: String,
        settings: FolderDisplaySettings,
    ): File? {
        logcat { "#collection $bookshelfId $collectionId" }
        val collection = collectionRepository.flow(collectionId).first()
            ?: return null

        logcat { "collection: $collection" }
        val sortType = bookNavigationService.resolveCollectionSortType(collection, settings)

        val modelFlow = when (collection) {
            is BasicCollection -> {
                val collectionFile = CollectionFile(collectionId, bookshelfId, path)
                if (isNext) {
                    collectionFileRepository.flowNextCollectionFile(collectionFile, sortType)
                } else {
                    collectionFileRepository.flowPrevCollectionFile(collectionFile, sortType)
                }
            }

            is SmartCollection -> {
                if (isNext) {
                    fileRepository.nextFile(
                        bookshelfId,
                        path,
                        collection.searchCondition,
                        sortType,
                    )
                } else {
                    fileRepository.prevFile(
                        bookshelfId,
                        path,
                        collection.searchCondition,
                        sortType,
                    )
                }
            }
        }
        return modelFlow.first()
    }

    private inline fun resolveBookResult(block: () -> File?): Resource<Book, Error> =
        runCatchingCancellable(block).fold(
            onSuccess = { file ->
                val book = bookNavigationService.validateBook(file)
                if (book != null) {
                    Resource.Success(book)
                } else {
                    Resource.Error(Error.NotFound)
                }
            },
            onFailure = {
                logcat { "GetNextBookInteractor: $it" }
                Resource.Error(Error.System)
            },
        )
}
