/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

@AssistedInject
internal class ComicViewerAppViewModel(
    @Assisted allowNavigationRestored: Boolean,
    val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
) : ViewModel() {

    val shouldKeepSplash = MutableStateFlow(true)
    val isInitialized = MutableStateFlow(false)

    init {
        if (allowNavigationRestored) {
            viewModelScope.launch {
                if (manageDisplaySettingsUseCase.settings.first().restoreOnLaunch) {
                    restoreNavigationWithTimeout()
                } else {
                    completeInit()
                }
            }
        } else if (allowNavigationRestored) {
            completeInit()
        }
    }

    val restoreNavigation: SharedFlow<RestoreNavigation>
        field = MutableSharedFlow<RestoreNavigation>()

    private fun restoreNavigationWithTimeout() {
        val restorationJob = viewModelScope.launch {
            restoreNavigation()
        }
        viewModelScope.launch {
            delay(RESTORE_TIMEOUT_MILLIS.milliseconds)
            restorationJob.cancel()
            completeInit()
        }
    }

    private suspend fun restoreNavigation() {
        val history = getNavigationHistoryUseCase(EmptyRequest).first().fold({ it }, { null })
        if (history?.folderList.isNullOrEmpty()) {
            completeInit()
            return
        }

        val (folderList, book) = history.value
        val bookshelfId = folderList.first().bookshelfId

        if (folderList.size == 1) {
            navigateToSingleFolder(bookshelfId, folderList.first().path, book.path)
        } else {
            navigateToNestedFolders(bookshelfId, folderList, book.path)
        }
    }

    private suspend fun navigateToSingleFolder(
        bookshelfId: BookshelfId,
        path: String,
        bookPath: String,
    ) {
        restoreNavigation.emit(
            RestoreNavigation(
                bookshelfId = bookshelfId,
                path = path,
                restorePath = bookPath,
                onRestoreComplete = ::completeInit,
            ),
        )
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
            "bookshelf(${bookshelfId.value}) -> folder($path)"
        }
    }

    private suspend fun navigateToNestedFolders(
        bookshelfId: BookshelfId,
        folderList: List<Folder>,
        bookPath: String,
    ) {
        // Navigate to first folder
        restoreNavigation.emit(
            RestoreNavigation(
                bookshelfId = bookshelfId,
                path = folderList.first().path,
                restorePath = null,
            ),
        )
        restoreNavigation.emit(
            RestoreNavigation(
                bookshelfId = bookshelfId,
                path = folderList.first().path,
                restorePath = null,
            ),
        )
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
            "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
        }

        // Navigate through intermediate folders
        folderList.drop(1).dropLast(1).forEach { folder ->

            restoreNavigation.emit(
                RestoreNavigation(
                    bookshelfId = bookshelfId,
                    path = folder.path,
                    restorePath = null,
                ),
            )
            logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                "-> folder(${folder.path})"
            }
        }

        // Navigate to last folder with book restoration

        restoreNavigation.emit(
            RestoreNavigation(
                bookshelfId = bookshelfId,
                path = folderList.last().path,
                restorePath = bookPath,
                onRestoreComplete = ::completeInit,
            ),
        )
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
            "-> folder(${folderList.last().path}), $bookPath"
        }
    }

    fun completeInit() {
        shouldKeepSplash.value = false
        isInitialized.value = true
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(allowNavigationRestored: Boolean = true): ComicViewerAppViewModel
    }
}

private const val RESTORE_TIMEOUT_MILLIS = 3000L
