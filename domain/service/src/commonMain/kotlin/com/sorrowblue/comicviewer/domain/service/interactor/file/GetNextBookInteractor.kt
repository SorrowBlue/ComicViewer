package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
internal class GetNextBookInteractor(
    private val datastoreDataSource: DatastoreDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
) : GetNextBookUseCase() {
    override suspend fun run(request: Request): Resource<Book, Error> {
        val settings = datastoreDataSource.folderDisplaySettings.first()
        return when (val location = request.location) {
            is Location.Collection -> collection(
                request.isNext,
                location.collectionId,
                request.bookshelfId,
                request.path,
                settings.sortType,
            )

            Location.Folder -> folder(
                request.isNext,
                request.bookshelfId,
                request.path,
                settings.sortType,
            )
        }
    }

    private suspend fun folder(
        isNext: Boolean,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Resource<Book, Error> = runCatching {
        if (isNext) {
            fileLocalDataSource.nextFileModel(bookshelfId, path, sortType)
        } else {
            fileLocalDataSource.prevFileModel(bookshelfId, path, sortType)
        }
    }.fold({ modelFlow ->
        modelFlow.first().let {
            if (it is Book) {
                Resource.Success(it)
            } else {
                Resource.Error(Error.NotFound)
            }
        }
    }, {
        Resource.Error(Error.System)
    })

    private suspend fun collection(
        isNext: Boolean,
        collectionId: CollectionId,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Resource<Book, Error> = runCatching {
        if (isNext) {
            collectionFileLocalDataSource.flowNextCollectionFile(
                CollectionFile(collectionId, bookshelfId, path),
                sortType,
            )
        } else {
            collectionFileLocalDataSource.flowPrevCollectionFile(
                CollectionFile(collectionId, bookshelfId, path),
                sortType,
            )
        }
    }.fold({ modelFlow ->
        modelFlow.first().let {
            if (it is Book) {
                Resource.Success(it)
            } else {
                Resource.Error(
                    Error.NotFound,
                )
            }
        }
    }, {
        Resource.Error(Error.System)
    })
}
