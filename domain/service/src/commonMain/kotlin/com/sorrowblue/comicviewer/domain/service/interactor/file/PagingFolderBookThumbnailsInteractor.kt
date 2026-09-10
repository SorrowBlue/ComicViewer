/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.file

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFolderBookThumbnailsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking

@ContributesBinding(AppScope::class)
internal class PagingFolderBookThumbnailsInteractor(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val settingsRepository: SettingsRepository,
) : PagingFolderBookThumbnailsUseCase() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<BookThumbnail>> =
        bookshelfRepository.flow(request.bookshelfId).flatMapLatest { bookshelf ->
            if (bookshelf != null) {
                val file = fileRepository.findBy(request.bookshelfId, request.path)
                if (file is IFolder) {
                    fileRepository.pagingSourceBookThumbnail(
                        request.pagingConfig,
                        bookshelf,
                        file,
                    ) {
                        val settings =
                            runBlocking { settingsRepository.folderDisplaySettings.first() }
                        SearchCondition(
                            "",
                            SearchCondition.Range.InFolder(file.path),
                            SearchCondition.Period.None,
                            settings.sortType,
                            settings.showHiddenFiles,
                        )
                    }
                } else {
                    emptyFlow()
                }
            } else {
                emptyFlow()
            }
        }
}
