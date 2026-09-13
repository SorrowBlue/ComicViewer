/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.service.file.FileSortService
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

class ScanBookshelfUseCase(private val action: suspend (Request) -> Resource<List<File>, Error>) :
    OneShotUseCase<ScanBookshelfUseCase.Request, List<File>, ScanBookshelfUseCase.Error>() {

    @Inject
    constructor(
        bookshelfRepository: BookshelfRepository,
        fileRepository: FileRepository,
        remoteStorageClientFactory: RemoteStorageClient.Factory,
        settingsRepository: SettingsRepository,
        fileSortService: FileSortService,
    ) : this({ request ->
        val bookshelf = bookshelfRepository.flow(request.bookshelfId).first()
        if (bookshelf != null) {
            val rootFolder = fileRepository.root(request.bookshelfId)
            if (rootFolder != null) {
                val supportExtension =
                    settingsRepository.folderSettings
                        .first()
                        .supportExtension
                        .map { it.extension }
                val resolveImageFolder =
                    settingsRepository.folderSettings.first().resolveImageFolder
                remoteStorageClientFactory
                    .create(
                        bookshelf,
                    ).nestedListFiles(
                        bookshelf,
                        rootFolder,
                        request.process,
                        resolveImageFolder,
                        supportExtension,
                        fileRepository,
                        fileSortService,
                    )
            }
        }
        Resource.Success(emptyList())
    })

    data class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, File) -> Unit,
    ) : OneShotUseCase.Request

    enum class Error : Resource.AppError {
        System,
    }

    override suspend fun run(request: Request): Resource<List<File>, Error> = action(request)
}

private suspend fun RemoteStorageClient.nestedListFiles(
    bookshelf: Bookshelf,
    file: File,
    process: suspend (Bookshelf, File) -> Unit,
    resolveImageFolder: Boolean,
    supportExtensions: List<String>,
    fileRepository: FileRepository,
    fileSortService: FileSortService,
) {
    val fileModelList = fileSortService.sortedIndex(
        listFiles(file, resolveImageFolder) {
            fileSortService.filter(it, supportExtensions)
        },
    )
    fileRepository.updateHistory(file, fileModelList)
    fileModelList.forEach {
        process(bookshelf, it)
    }
    fileModelList
        .filterIsInstance<IFolder>()
        .forEach {
            nestedListFiles(
                bookshelf,
                it,
                process,
                resolveImageFolder,
                supportExtensions,
                fileRepository,
                fileSortService,
            )
        }
}
