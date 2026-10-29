/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Observes paginated [BookThumbnail] items inside a folder on a bookshelf.
 */
fun interface PagingFolderBookThumbnailsUseCase :
    PagingUseCase<PagingFolderBookThumbnailsUseCase.Request, BookThumbnail> {

    /**
     * Request parameters for [PagingFolderBookThumbnailsUseCase].
     *
     * @property bookshelfId The ID of the bookshelf.
     * @property path The path of the folder.
     * @property pagingConfig Configuration for pagination.
     */
    data class Request(
        val bookshelfId: BookshelfId,
        val path: String,
        val pagingConfig: PagingConfig,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingFolderBookThumbnailsUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val settingsRepository: SettingsRepository,
) : PagingFolderBookThumbnailsUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(request: PagingFolderBookThumbnailsUseCase.Request) =
        bookshelfRepository.flow(request.bookshelfId)
            .distinctUntilChanged()
            .flatMapLatest { bookshelf ->
                if (bookshelf != null) {
                    fileRepository.flow(request.bookshelfId, request.path)
                        .catch { e ->
                            if (e is CancellationException) throw e
                            emit(null)
                        }
                        .distinctUntilChanged { old, new ->
                            old?.bookshelfId == new?.bookshelfId &&
                                old?.path == new?.path &&
                                (old is IFolder) == (new is IFolder)
                        }
                        .flatMapLatest { file ->
                            if (file is IFolder) {
                                settingsRepository.folderDisplaySettings
                                    .map { settings ->
                                        settings.currentSortType(
                                            file.bookshelfId,
                                            file.path,
                                        ) to settings.showHiddenFiles
                                    }
                                    .distinctUntilChanged()
                                    .flatMapLatest { (sortType, showHiddenFiles) ->
                                        fileRepository.pagingSourceBookThumbnail(
                                            request.pagingConfig,
                                            bookshelf,
                                            file,
                                        ) {
                                            SearchCondition(
                                                "",
                                                SearchCondition.Range.InFolder(file.path),
                                                SearchCondition.Period.None,
                                                sortType,
                                                showHiddenFiles,
                                            )
                                        }
                                    }
                            } else {
                                emptyFlow()
                            }
                        }
                } else {
                    emptyFlow()
                }
            }
}
