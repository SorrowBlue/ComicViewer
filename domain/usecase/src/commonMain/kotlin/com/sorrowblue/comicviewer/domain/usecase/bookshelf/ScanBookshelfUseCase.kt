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
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Scans the file hierarchy of a bookshelf and invokes a callback for each discovered file.
 */
abstract class ScanBookshelfUseCase :
    OneShotUseCase<ScanBookshelfUseCase.Request, Unit, ScanBookshelfUseCase.Error>() {

    /**
     * Request parameters for [ScanBookshelfUseCase].
     *
     * @property bookshelfId The ID of the bookshelf to scan.
     * @property process Callback invoked for each discovered [File] on the [Bookshelf].
     */
    class Request(val bookshelfId: BookshelfId, val process: suspend (Bookshelf, File) -> Unit)

    /**
     * Errors that can occur during bookshelf scanning.
     */
    enum class Error : Resource.AppError {
        /**
         * Indicates that a system error occurred during scanning.
         */
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

    override suspend fun run(request: Request): Resource<Unit, Error> = runCatchingCancellable {
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
                    supportExtensions = supportExtension,
                    listFiles = { file, filter ->
                        client.listFiles(file, resolveImageFolder, filter)
                    },
                    onFolderFilesScanned = { parent, files ->
                        fileRepository.syncFolderFiles(parent, files)
                    },
                    onFileDiscovered = { file ->
                        request.process(bookshelf, file)
                    },
                )
            }
        }
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = {
            logcat(LogPriority.ERROR) { it.asLog() }
            Resource.Error(Error.System)
        },
    )
}
