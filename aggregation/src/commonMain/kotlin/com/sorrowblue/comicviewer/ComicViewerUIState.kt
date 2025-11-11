package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sorrowblue.comicviewer.app.MainViewModel
import com.sorrowblue.comicviewer.app.rememberAdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKey
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteState
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigation3State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

interface ComicViewerUIState {
    val navigation3State: Navigation3State
    val adaptiveNavigationSuiteState: AdaptiveNavigationSuiteState

    fun onNavigationHistoryRestore()
}

@Composable
context(context: ComicViewerUIContext)
fun rememberComicViewerUIState(allowNavigationRestored: Boolean = true): ComicViewerUIState {
    val mainViewModel = viewModel { MainViewModel() }
    val navigation3State: Navigation3State = rememberNavigation3State()
    val adaptiveNavigationSuiteState = rememberAdaptiveNavigationSuiteState(navigation3State)
    val coroutineScope = rememberCoroutineScope()
    return remember {
        ComicViewerUIStateImpl(
            allowNavigationRestored = allowNavigationRestored,
            coroutineScope = coroutineScope,
            manageDisplaySettingsUseCase = context.manageDisplaySettingsUseCase,
            getNavigationHistoryUseCase = context.getNavigationHistoryUseCase,
            completeInit = {
                mainViewModel.shouldKeepSplash.value = false
                mainViewModel.isInitialized.value = true
            },
        )
    }.apply {
        this.navigation3State = navigation3State
        this.adaptiveNavigationSuiteState = adaptiveNavigationSuiteState
    }
}

private class ComicViewerUIStateImpl(
    allowNavigationRestored: Boolean,
    private val coroutineScope: CoroutineScope,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    private val completeInit: () -> Unit,
) : ComicViewerUIState {
    override lateinit var navigation3State: Navigation3State
    override lateinit var adaptiveNavigationSuiteState: AdaptiveNavigationSuiteState

    var isNavigationRestored by mutableStateOf(false)

    init {
        if (allowNavigationRestored) {
            if (!isNavigationRestored) {
                coroutineScope.launch {
                    if (manageDisplaySettingsUseCase.settings.first().restoreOnLaunch) {
                        cancelJob(
                            coroutineScope,
                            3000,
                            ::completeRestoreHistory,
                            ::restoreNavigation,
                        )
                    } else {
                        completeRestoreHistory()
                    }
                }
            } else {
                completeRestoreHistory()
            }
        }
    }

    override fun onNavigationHistoryRestore() {
        logcat { "onNavigationHistoryRestore" }
        completeRestoreHistory()
    }

    private fun restoreNavigation(): Job = coroutineScope.launch {
        val history = getNavigationHistoryUseCase(EmptyRequest).first().fold({ it }, { null })
        if (history?.folderList.isNullOrEmpty()) {
            completeRestoreHistory()
        } else {
            val (folderList, book) = history.value
            val bookshelfId = folderList.first().bookshelfId
            if (folderList.size == 1) {
                navigation3State.addToBackStack(
                    BookshelfKey.Folder(
                        bookshelfId = bookshelfId,
                        path = folderList.first().path,
                        restorePath = book.path,
                    ),
                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                }
            } else {
                navigation3State.addToBackStack(
                    BookshelfKey.Folder(
                        bookshelfId = bookshelfId,
                        path = folderList.first().path,
                        restorePath = null,
                    ),
                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "bookshelf(${bookshelfId.value}) -> folder(${folderList.first().path})"
                }
                folderList.drop(1).dropLast(1).forEach { folder ->
                    navigation3State.addToBackStack(
                        BookshelfKey.Folder(
                            bookshelfId = bookshelfId,
                            path = folderList.first().path,
                            restorePath = null,
                        ),
                    )
                    logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                        "-> folder(${folder.path})"
                    }
                }
                navigation3State.addToBackStack(
                    BookshelfKey.Folder(
                        bookshelfId = bookshelfId,
                        path = folderList.last().path,
                        restorePath = book.path,
                    ),
                )
                logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
                    "-> folder${folderList.last().path}, ${book.path}"
                }
            }
        }
    }

    private fun completeRestoreHistory() {
        completeInit()
        isNavigationRestored = true
    }

    /**
     * Cancel job
     *
     * @param scope
     * @param waitTimeMillis
     * @param onCancel
     * @param action
     * @receiver
     * @receiver
     */
    private fun cancelJob(
        scope: CoroutineScope,
        waitTimeMillis: Long,
        onCancel: () -> Unit,
        action: () -> Unit,
    ) {
        val job = scope.launch {
            action()
        }
        scope.launch {
            delay(waitTimeMillis)
            onCancel()
            job.cancel()
        }
    }
}
