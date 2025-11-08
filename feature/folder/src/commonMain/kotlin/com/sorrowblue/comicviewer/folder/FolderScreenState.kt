package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResult
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import com.sorrowblue.comicviewer.framework.ui.rememberAdaptiveNavigationSuiteScaffoldState
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat

internal sealed interface FolderScreenEvent {
    data object Restore : FolderScreenEvent
}

@Stable
internal interface FolderScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val events: EventFlow<FolderScreenEvent>
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState
    val uiState: FolderScreenUiState

    fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>)

    fun onSortTypeSelectScreenResult(result: SortTypeSelectScreenResult)
}

@Composable
context(context: FolderScreenContext)
internal fun rememberFolderScreenState(
    bookshelfId: BookshelfId,
    path: String,
    restorePath: String?,
): FolderScreenState {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberSaveable(
        saver = FolderScreenStateImpl.saver(
            bookshelfId = bookshelfId,
            path = path,
            restorePath = restorePath,
            folderDisplaySettingsUseCase = context.displaySettingsUseCase,
            getFileUseCase = context.getFileUseCase,
            scope = coroutineScope,
        ),
    ) {
        FolderScreenStateImpl(
            bookshelfId = bookshelfId,
            path = path,
            restorePath = restorePath,
            folderDisplaySettingsUseCase = context.displaySettingsUseCase,
            getFileUseCase = context.getFileUseCase,
            scope = coroutineScope,
        )
    }.apply {
        this.lazyPagingItems = rememberPagingItems {
            context.pagingFileUseCase(
                PagingFileUseCase.Request(
                    PagingConfig(20),
                    bookshelfId,
                    path,
                ),
            )
        }
        scope = coroutineScope
        lazyGridState = rememberLazyGridState()
        scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    }

    LaunchedEffect(state.lazyPagingItems.loadState) {
        state.onLoadStateChange(state.lazyPagingItems)
    }

    return state
}

private class FolderScreenStateImpl(
    private val bookshelfId: BookshelfId,
    private val path: String,
    private val restorePath: String?,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    var scope: CoroutineScope,
    getFileUseCase: GetFileUseCase,
) : FolderScreenState {
    override lateinit var lazyPagingItems: LazyPagingItems<File>
    override lateinit var lazyGridState: LazyGridState
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState

    override val events = EventFlow<FolderScreenEvent>()
    override var uiState by mutableStateOf(FolderScreenUiState())
        private set

    var isRestored by mutableStateOf(false)

    init {
        uiState =
            uiState.copy(
                folderListUiState = uiState.folderListUiState.copy(
                    emphasisPath = restorePath.orEmpty(),
                ),
            )
        folderDisplaySettingsUseCase.settings
            .distinctUntilChanged()
            .onEach {
                uiState = uiState.copy(
                    folderAppBarUiState = uiState.folderAppBarUiState.copy(
                        fileListDisplay = it.fileListDisplay,
                        gridColumnSize = it.gridColumnSize,
                        showHiddenFile = it.showHiddenFiles,
                    ),
                    folderListUiState = uiState.folderListUiState.copy(
                        fileLazyVerticalGridUiState = uiState.folderListUiState.fileLazyVerticalGridUiState
                            .copy(
                                fileListDisplay = it.fileListDisplay,
                                columnSize = it.gridColumnSize,
                                imageScale = it.imageScale,
                                imageFilterQuality = it.imageFilterQuality,
                                fontSize = it.fontSize,
                                showThumbnails = it.showThumbnails,
                            ),
                    ),
                    folderScopeOnly = it.folderScopeOnlyList.any { scopeOnly ->
                        scopeOnly.bookshelfId ==
                            bookshelfId &&
                            scopeOnly.path == path
                    },
                    sortType =
                    it.folderScopeOnlyList
                        .find { scopeOnly ->
                            scopeOnly.bookshelfId == bookshelfId &&
                                scopeOnly.path == path
                        }?.sortType
                        ?: it.sortType,
                )
            }.launchIn(scope)
        getFileUseCase(GetFileUseCase.Request(bookshelfId, path))
            .onEach {
                when (it) {
                    is Resource.Error -> Unit
                    is Resource.Success -> {
                        uiState = uiState.copy(
                            folderAppBarUiState = uiState.folderAppBarUiState.copy(
                                title = it.data.name,
                            ),
                        )
                    }
                }
            }.launchIn(scope)
    }

    private fun refreshItems() {
        lazyPagingItems.refresh()
    }

    override fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>) {
        logcat {
            "isRestored: $isRestored, restorePath: $restorePath, itemCount: ${lazyPagingItems.itemCount}"
        }
        if (!isRestored && restorePath != null && 0 < lazyPagingItems.itemCount) {
            val index = lazyPagingItems.indexOf { it?.path == restorePath }
            if (0 <= index) {
                isRestored = true
                runCatching {
                    scope.launch {
                        lazyGridState.scrollToItem(min(index, lazyPagingItems.itemCount - 1))
                    }
                }.onFailure {
                    logcat { it.asLog() }
                }
                events.tryEmit(FolderScreenEvent.Restore)
            } else if (!lazyPagingItems.loadState.isLoading) {
                events.tryEmit(FolderScreenEvent.Restore)
            }
        }
        if (lazyPagingItems.loadState.refresh is LoadState.Error) {
            ((lazyPagingItems.loadState.refresh as LoadState.Error).error as? PagingException)?.let {
//                scope.launch {
//                    when (it) {
//                        is PagingException.InvalidAuth -> scaffoldState.snackbarHostState.showSnackbar(
//                            "認証エラー"
//                        )
//
//                        is PagingException.InvalidServer -> scaffoldState.snackbarHostState.showSnackbar(
//                            "サーバーエラー"
//                        )
//
//                        is PagingException.NoNetwork -> scaffoldState.snackbarHostState.showSnackbar(
//                            "ネットワークエラー"
//                        )
//
//                        is PagingException.NotFound -> scaffoldState.snackbarHostState.showSnackbar(
//                            "見つかりませんでした"
//                        )
//                    }
//                }
            }
        }
    }

    override fun onSortTypeSelectScreenResult(result: SortTypeSelectScreenResult) {
        scope.launch {
            var refresh = false
            folderDisplaySettingsUseCase.edit { settings ->
                val beforeFolderScopeOnly =
                    settings.folderScopeOnlyList.find {
                        it.bookshelfId == bookshelfId &&
                            it.path == path
                    }
                when {
                    result.folderScopeOnly -> {
                        if (beforeFolderScopeOnly == null) {
                            refresh = true
                            settings.copy(
                                folderScopeOnlyList =
                                settings.folderScopeOnlyList + FolderScopeOnly(
                                    bookshelfId,
                                    path,
                                    result.sortType,
                                ),
                            )
                        } else if (beforeFolderScopeOnly.sortType != result.sortType) {
                            refresh = true
                            val new = FolderScopeOnly(
                                bookshelfId,
                                path,
                                result.sortType,
                            )
                            settings.copy(
                                folderScopeOnlyList =
                                settings.folderScopeOnlyList - beforeFolderScopeOnly + new,
                            )
                        } else {
                            settings
                        }
                    }

                    !result.folderScopeOnly && beforeFolderScopeOnly != null -> {
                        refresh = true
                        settings.copy(
                            folderScopeOnlyList =
                            settings.folderScopeOnlyList - beforeFolderScopeOnly,
                        )
                    }

                    settings.sortType != result.sortType -> {
                        refresh = true
                        settings.copy(sortType = result.sortType)
                    }

                    else -> {
                        settings
                    }
                }
            }
            if (refresh) {
                refreshItems()
            }
        }
    }

    fun onReSelected() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.animateScrollToItem(0)
            }
        }
    }

    private fun updateFolderDisplaySettings(
        edit: (FolderDisplaySettings) -> FolderDisplaySettings,
    ) {
        scope.launch {
            folderDisplaySettingsUseCase.edit(edit)
        }
    }

    companion object {
        fun saver(
            bookshelfId: BookshelfId,
            path: String,
            restorePath: String?,
            scope: CoroutineScope,
            folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
            getFileUseCase: GetFileUseCase,
        ) = Saver<FolderScreenStateImpl, Boolean>(
            save = {
                it.isRestored
            },
            restore = {
                FolderScreenStateImpl(
                    bookshelfId = bookshelfId,
                    path = path,
                    restorePath = restorePath,
                    scope = scope,
                    folderDisplaySettingsUseCase = folderDisplaySettingsUseCase,
                    getFileUseCase = getFileUseCase,
                ).apply {
                    isRestored = it
                }
            },
        )
    }
}
