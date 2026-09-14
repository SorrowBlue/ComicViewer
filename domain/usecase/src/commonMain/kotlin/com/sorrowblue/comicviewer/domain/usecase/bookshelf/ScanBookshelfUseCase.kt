/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.repository.storage.RemoteStorageClient
import com.sorrowblue.comicviewer.domain.service.file.FileHierarchyScanService
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
        fileHierarchyScanService: FileHierarchyScanService,
    ) : this({ request ->
        val bookshelf = bookshelfRepository.flow(request.bookshelfId).first()
        if (bookshelf != null) {
            val rootFolder = fileRepository.root(request.bookshelfId)
            if (rootFolder != null) {
                val folderSettings = settingsRepository.folderSettings.first()
                val supportExtension =
                    folderSettings.supportExtension
                        .map { it.extension }
                val resolveImageFolder = folderSettings.resolveImageFolder
                val client = remoteStorageClientFactory.create(bookshelf)
                fileHierarchyScanService.scanHierarchy(
                    root = rootFolder,
                    resolveImageFolder = resolveImageFolder,
                    supportExtensions = supportExtension,
                    listFiles = { file, filter ->
                        client.listFiles(file, resolveImageFolder, filter)
                    },
                    onFolderFilesScanned = { parent, files ->
                        fileRepository.updateHistory(parent, files)
                    },
                    onFileDiscovered = { file ->
                        request.process(bookshelf, file)
                    },
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
