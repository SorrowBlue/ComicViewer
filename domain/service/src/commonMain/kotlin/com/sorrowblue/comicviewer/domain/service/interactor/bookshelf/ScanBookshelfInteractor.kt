package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.SortUtil
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

@Factory
internal class ScanBookshelfInteractor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
    private val remoteDataSourceFactory: RemoteDataSource.Factory,
    private val datastoreDataSource: DatastoreDataSource,
) : ScanBookshelfUseCase() {

    override suspend fun run(request: Request): Resource<List<File>, Error> {
        val bookshelf = bookshelfLocalDataSource.flow(request.bookshelfId).first()
        if (bookshelf != null) {
            val rootFolder = fileLocalDataSource.root(request.bookshelfId)
            if (rootFolder != null) {
                val supportExtension =
                    datastoreDataSource.folderSettings.first().supportExtension.map { it.extension }
                val resolveImageFolder =
                    datastoreDataSource.folderSettings.first().resolveImageFolder
                remoteDataSourceFactory.create(
                    bookshelf
                ).nestedListFiles(
                    bookshelf,
                    rootFolder,
                    request.process,
                    resolveImageFolder,
                    supportExtension
                )
            }
        }
        return Resource.Success(emptyList())
    }

    private suspend fun RemoteDataSource.nestedListFiles(
        bookshelf: Bookshelf,
        file: File,
        process: suspend (Bookshelf, File) -> Unit,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>,
    ) {
        val fileModelList = SortUtil.sortedIndex(
            listFiles(file, resolveImageFolder) {
                SortUtil.filter(it, supportExtensions)
            }
        )
        fileLocalDataSource.updateHistory(file, fileModelList)
        fileModelList.forEach {
            process(bookshelf, it)
        }
        fileModelList.filterIsInstance<IFolder>()
            .forEach {
                nestedListFiles(
                    bookshelf,
                    it,
                    process,
                    resolveImageFolder,
                    supportExtensions
                )
            }
    }
}
