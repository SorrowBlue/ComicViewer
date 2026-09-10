/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.NavigationHistory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@ContributesBinding(AppScope::class)
internal class GetNavigationHistoryInteractor(
    private val fileRepository: FileRepository,
    private val bookshelfRepository: BookshelfRepository,
) : GetNavigationHistoryUseCase() {
    override fun run(request: EmptyRequest): Flow<Resource<NavigationHistory, Error>> {
        return fileRepository.lastHistory().map { file ->
            if (file != null) {
                val bookshelf = bookshelfRepository.flow(file.bookshelfId).first()
                if (bookshelf != null) {
                    val book = fileRepository.findBy(file.bookshelfId, file.path) as? Book
                    if (book != null) {
                        return@map Resource.Success(
                            NavigationHistory(getFolderList(bookshelf, book.parent), book),
                        )
                    }
                }
            }
            return@map Resource.Error(Error.System)
        }
    }

    private suspend fun getFolderList(bookshelf: Bookshelf, path: String): List<Folder> {
        val list = mutableListOf<Folder>()
        var parent: String? = path
        while (!parent.isNullOrEmpty()) {
            getFolder(bookshelf, parent)?.let {
                list.add(0, it)
                parent = it.parent
            } ?: kotlin.run {
                return emptyList()
            }
        }
        return list
    }

    private suspend fun getFolder(bookshelf: Bookshelf, path: String): Folder? =
        fileRepository.findBy(bookshelf.id, path) as? Folder
}
