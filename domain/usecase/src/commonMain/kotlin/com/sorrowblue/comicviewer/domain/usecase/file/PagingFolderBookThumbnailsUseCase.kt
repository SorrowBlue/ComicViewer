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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

fun interface PagingFolderBookThumbnailsUseCase :
    PagingUseCase<PagingFolderBookThumbnailsUseCase.Request, BookThumbnail> {

    class Request(val bookshelfId: BookshelfId, val path: String, val pagingConfig: PagingConfig)
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
        bookshelfRepository.flow(request.bookshelfId).flatMapLatest { bookshelf ->
            if (bookshelf != null) {
                val file = fileRepository.findBy(request.bookshelfId, request.path)
                if (file is IFolder) {
                    settingsRepository.folderDisplaySettings.flatMapLatest { settings ->
                        fileRepository.pagingSourceBookThumbnail(
                            request.pagingConfig,
                            bookshelf,
                            file,
                        ) {
                            SearchCondition(
                                "",
                                SearchCondition.Range.InFolder(file.path),
                                SearchCondition.Period.None,
                                settings.currentSortType(file.bookshelfId, file.path),
                                settings.showHiddenFiles,
                            )
                        }
                    }
                } else {
                    emptyFlow()
                }
            } else {
                emptyFlow()
            }
        }
}
