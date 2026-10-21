/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

fun interface PagingFileUseCase : PagingUseCase<PagingFileUseCase.Request, File> {

    data class Request(
        val pagingConfig: PagingConfig,
        val bookshelfId: BookshelfId,
        val path: String,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingFileUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val settingsRepository: SettingsRepository,
) : PagingFileUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(request: PagingFileUseCase.Request) = bookshelfRepository
        .flow(request.bookshelfId).filterNotNull().flatMapLatest { bookshelf ->
            val file = fileRepository.findBy(request.bookshelfId, request.path) as IFolder
            settingsRepository.folderDisplaySettings.flatMapLatest { settings ->
                fileRepository.pagingDataFlow(request.pagingConfig, bookshelf, file) {
                    SearchCondition(
                        "",
                        SearchCondition.Range.InFolder(file.path),
                        SearchCondition.Period.None,
                        settings.currentSortType(file.bookshelfId, file.path),
                        settings.showHiddenFiles,
                    )
                }
            }
        }
}
