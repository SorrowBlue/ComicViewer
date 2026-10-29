/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.NavigationHistory
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Observes the [NavigationHistory] for the most recently read book, including its parent folder
 * chain.
 */
abstract class GetNavigationHistoryUseCase :
    UseCase<Unit, NavigationHistory, GetNavigationHistoryUseCase.Error>() {

    /**
     * Errors that can occur when retrieving navigation history.
     */
    sealed interface Error : Resource.AppError {
        /**
         * Indicates that a system error occurred or the history target could not be resolved.
         */
        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetNavigationHistoryUseCaseImpl(
    private val fileRepository: FileRepository,
    private val bookshelfRepository: BookshelfRepository,
) : GetNavigationHistoryUseCase() {

    override fun run(request: Unit): Flow<Resource<NavigationHistory, Error>> {
        return fileRepository.lastHistory()
            .distinctUntilChanged()
            .map<File?, Resource<NavigationHistory, Error>> { file ->
                val book = file as? Book ?: return@map Resource.Error(Error.System)
                runCatchingCancellable {
                    val bookshelf = bookshelfRepository.flow(book.bookshelfId).first()
                    if (bookshelf != null && !bookshelf.isDeleted) {
                        Resource.Success(
                            NavigationHistory(getFolderList(bookshelf, book.parent), book),
                        )
                    } else {
                        Resource.Error(Error.System)
                    }
                }.getOrElse {
                    logcat(LogPriority.ERROR) { it.asLog() }
                    Resource.Error(Error.System)
                }
            }.catch { e ->
                if (e is CancellationException) throw e
                logcat(LogPriority.ERROR) { e.asLog() }
                emit(Resource.Error(Error.System))
            }
    }

    private suspend fun getFolderList(bookshelf: Bookshelf, path: String): List<Folder> {
        val list = ArrayDeque<Folder>()
        val visited = mutableSetOf<String>()
        var parent: String? = path
        while (!parent.isNullOrEmpty()) {
            if (!visited.add(parent)) {
                return emptyList()
            }
            getFolder(bookshelf, parent)?.let {
                list.addFirst(it)
                parent = it.parent
            } ?: kotlin.run {
                return emptyList()
            }
        }
        return list.toList()
    }

    private suspend fun getFolder(bookshelf: Bookshelf, path: String): Folder? =
        fileRepository.findBy(bookshelf.id, path) as? Folder
}
