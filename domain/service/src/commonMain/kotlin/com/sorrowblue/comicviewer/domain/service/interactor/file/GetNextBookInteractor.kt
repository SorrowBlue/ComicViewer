package com.sorrowblue.comicviewer.domain.service.interactor.file

import com.sorrowblue.comicviewer.domain.model.Resource
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
internal class GetNextBookInteractor(
    private val datastoreDataSource: DatastoreDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
) : GetNextBookUseCase() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<Resource<Book, GetLibraryInfoError>> {
        return datastoreDataSource.folderDisplaySettings.flatMapLatest { settings ->
            when (val location = request.location) {
                is Location.Favorite -> favorite(
                    request.isNext,
                    location.favoriteId,
                    request.bookshelfId,
                    request.path,
                    settings.sortType
                )

                Location.Folder -> folder(
                    request.isNext,
                    request.bookshelfId,
                    request.path,
                    settings.sortType
                )
            }
        }
    }

    private fun folder(
        isNext: Boolean,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<Resource<Book, GetLibraryInfoError>> {
        return runCatching {
            if (isNext) {
                fileLocalDataSource.nextFileModel(bookshelfId, path, sortType)
            } else {
                fileLocalDataSource.prevFileModel(bookshelfId, path, sortType)
            }
        }.fold({ modelFlow ->
            modelFlow.map {
                if (it is Book) {
                    Resource.Success(it)
                } else {
                    Resource.Error(GetLibraryInfoError.NOT_FOUND)
                }
            }
        }, {
            flowOf(Resource.Error(GetLibraryInfoError.SYSTEM_ERROR))
        })
    }

    private fun favorite(
        isNext: Boolean,
        favoriteId: FavoriteId,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<Resource<Book, GetLibraryInfoError>> {
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
            }
        }.fold({ modelFlow ->
            modelFlow.map {
                if (it is Book) {
                    Resource.Success(it)
                } else {
                    Resource.Error(
                        GetLibraryInfoError.NOT_FOUND
                    )
                }
            }
        }, {
            flowOf(Resource.Error(GetLibraryInfoError.SYSTEM_ERROR))
        })
    }
}
