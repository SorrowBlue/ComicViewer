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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

abstract class ScanBookshelfUseCase :
    OneShotUseCase<ScanBookshelfUseCase.Request, List<File>, ScanBookshelfUseCase.Error>() {

    data class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, File) -> Unit,
    )

    enum class Error : Resource.AppError {
        System,
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class ScanBookshelfUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val remoteStorageClientFactory: RemoteStorageClient.Factory,
    private val settingsRepository: SettingsRepository,
    private val fileHierarchyScanService: FileHierarchyScanService,
) : ScanBookshelfUseCase() {

    override suspend fun run(request: Request): Resource<List<File>, Error> {
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
        return Resource.Success(emptyList())
    }
}
