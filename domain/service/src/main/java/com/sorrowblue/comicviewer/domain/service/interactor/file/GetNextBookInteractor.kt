package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

internal class GetNextBookInteractor @Inject constructor(
    private val datastoreDataSource: DatastoreDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
) : GetNextBookUseCase() {

    override fun run(request: Request): Flow<Result<Book, GetLibraryInfoError>> {
        val sortType =
            runBlocking { datastoreDataSource.folderDisplaySettings.first() }.sortType
        return when (val location = request.location) {
            is Location.Favorite -> favorite(
                request.isNext,
                location.favoriteId,
                request.bookshelfId,
                request.path,
                sortType
            )

            Location.Folder -> folder(request.isNext, request.bookshelfId, request.path, sortType)
        }
    }

    private fun folder(
        isNext: Boolean,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<Result<Book, GetLibraryInfoError>> {
        return runCatching {
            if (isNext) {
                fileLocalDataSource.nextFileModel(bookshelfId, path, sortType)
            } else {
                fileLocalDataSource.prevFileModel(bookshelfId, path, sortType)
            }.map {
                if (it is Book) {
                    Result.Success(it)
                } else {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }
            }
        }.fold({ modelFlow ->
            modelFlow.map {
                if (it.dataOrNull != null) {
                    Result.Success(it.dataOrNull!!)
                } else {
                    Result.Error(
                        GetLibraryInfoError.NOT_FOUND
                    )
                }
            }
        }, {
            flowOf(Result.Error(GetLibraryInfoError.SYSTEM_ERROR))
        })
    }

    private fun favorite(
        isNext: Boolean,
        favoriteId: FavoriteId,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<Result<Book, GetLibraryInfoError>> {
        return runCatching {
            if (isNext) {
                favoriteFileLocalDataSource.flowNextFavoriteFile(
                    FavoriteFile(favoriteId, bookshelfId, path),
                    sortType
                )
            } else {
                favoriteFileLocalDataSource.flowPrevFavoriteFile(
                    FavoriteFile(favoriteId, bookshelfId, path),
                    sortType
                )
            }.map {
                if (it is Book) {
                    Result.Success(it)
                } else {
                    Result.Error(GetLibraryInfoError.NOT_FOUND)
                }
            }
        }.fold({ modelFlow ->
            modelFlow.map {
                if (it.dataOrNull != null) {
                    Result.Success(it.dataOrNull!!)
                } else {
                    Result.Error(
                        GetLibraryInfoError.NOT_FOUND
                    )
                }
            }
        }, {
            flowOf(Result.Error(GetLibraryInfoError.SYSTEM_ERROR))
        })
    }
}
