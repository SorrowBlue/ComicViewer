package com.sorrowblue.comicviewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.sorrowblue.comicviewer.app.MainViewModel
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfFolderNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfNavKey
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.rememberNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic
import logcat.LogPriority
import logcat.logcat

interface ComicViewerUIState {
    val navigator: Navigator

    fun onNavigationHistoryRestore()
}

@Composable
context(context: ComicViewerUIContext)
fun rememberComicViewerUIState(
    allowNavigationRestored: Boolean = true,
    mainViewModel: MainViewModel = viewModel { MainViewModel() },
): ComicViewerUIState {
    val navigator = rememberNavigator(
        startKey = BookshelfNavKey,
        topLevelRoutes = context.navigationKeys.sortedBy { it.order }.toSet(),
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                contextual(SnapshotStateListSerializer(PolymorphicSerializer(NavKey::class)))
                polymorphic(NavKey::class) {
                    context.navKeySubclassMap.forEach {
                        subclass(it.first, it.second)
                    }
                }
            }
        },
    )
    val coroutineScope = rememberCoroutineScope()
    return remember(
        allowNavigationRestored,
        context.manageDisplaySettingsUseCase,
        context.getNavigationHistoryUseCase,
    ) {
        ComicViewerUIStateImpl(
            navigator = navigator,
            allowNavigationRestored = allowNavigationRestored,
            coroutineScope = coroutineScope,
            manageDisplaySettingsUseCase = context.manageDisplaySettingsUseCase,
            getNavigationHistoryUseCase = context.getNavigationHistoryUseCase,
            completeInit = {
                mainViewModel.shouldKeepSplash.value = false
                mainViewModel.isInitialized.value = true
            },
        )
    }
}

private class ComicViewerUIStateImpl(
    override val navigator: Navigator,
    allowNavigationRestored: Boolean,
    private val coroutineScope: CoroutineScope,
    private val manageDisplaySettingsUseCase: ManageDisplaySettingsUseCase,
    private val getNavigationHistoryUseCase: GetNavigationHistoryUseCase,
    private val completeInit: () -> Unit,
) : ComicViewerUIState {
    private var isNavigationRestored by mutableStateOf(false)

    init {
        if (allowNavigationRestored && !isNavigationRestored) {
            coroutineScope.launch {
                if (manageDisplaySettingsUseCase.settings.first().restoreOnLaunch) {
                    restoreNavigationWithTimeout()
                } else {
                    completeRestoreHistory()
                }
            }
        } else if (allowNavigationRestored) {
            completeRestoreHistory()
        }
    }

    override fun onNavigationHistoryRestore() {
        logcat { "onNavigationHistoryRestore" }
        completeRestoreHistory()
    }

    private fun restoreNavigationWithTimeout() {
        val restorationJob = coroutineScope.launch {
            restoreNavigation()
        }
        coroutineScope.launch {
            delay(RESTORE_TIMEOUT_MILLIS)
            restorationJob.cancel()
            completeRestoreHistory()
        }
    }

    private fun restoreNavigation(): Job = coroutineScope.launch {
        val history = getNavigationHistoryUseCase(EmptyRequest).first().fold({ it }, { null })
        if (history?.folderList.isNullOrEmpty()) {
            completeRestoreHistory()
            return@launch
        }

        val (folderList, book) = history.value
        val bookshelfId = folderList.first().bookshelfId

        if (folderList.size == 1) {
            navigateToSingleFolder(bookshelfId, folderList.first().path, book.path)
        } else {
            navigateToNestedFolders(bookshelfId, folderList, book.path)
        }
    }

    private fun navigateToSingleFolder(bookshelfId: BookshelfId, path: String, bookPath: String) {
        navigator.navigate(
            BookshelfFolderNavKey(
                bookshelfId = bookshelfId,
                path = path,
                restorePath = bookPath,
                onRestoreComplete = ::onNavigationHistoryRestore,
            ),
        )
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
            "bookshelf(${bookshelfId.value}) -> folder($path)"
        }
    }

    private fun navigateToNestedFolders(
        bookshelfId: BookshelfId,
        folderList: List<com.sorrowblue.comicviewer.domain.model.file.Folder>,
        bookPath: String,
    ) {
        // Navigate to first folder
        navigator.navigate(
            BookshelfFolderNavKey(
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
            navigator.navigate(
                BookshelfFolderNavKey(
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
        navigator.navigate(
            BookshelfFolderNavKey(
                bookshelfId = bookshelfId,
                path = folderList.last().path,
                restorePath = bookPath,
                onRestoreComplete = ::onNavigationHistoryRestore,
            ),
        )
        logcat("RESTORE_NAVIGATION", LogPriority.INFO) {
            "-> folder(${folderList.last().path}), $bookPath"
        }
    }

    private fun completeRestoreHistory() {
        completeInit()
        isNavigationRestored = true
    }

    companion object {
        private const val RESTORE_TIMEOUT_MILLIS = 3000L
    }
}
