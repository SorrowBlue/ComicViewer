package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.LoadState
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderListUiState
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResult
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
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
    val snackbarHostState: SnackbarHostState

    fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>)

    fun onSortTypeSelectScreenResult(result: SortTypeSelectScreenResult)

    fun onSortClick(sortType: SortType)

    fun onFolderScopeOnlyClick()

    fun onRefresh()
}

@Composable
context(context: FolderScreenContext)
internal fun rememberFolderScreenState(
    bookshelfId: BookshelfId,
    path: String,
    restorePath: String?,
    showSearch: Boolean,
): FolderScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyPagingItems = rememberPagingItems {
        context.pagingFileUseCase(
            PagingFileUseCase.Request(
                PagingConfig(20),
                bookshelfId,
                path,
            ),
        )
    }
    val state = rememberSaveable(
        saver = FolderScreenStateImpl.saver {
            FolderScreenStateImpl(
                bookshelfId = bookshelfId,
                path = path,
                restorePath = restorePath,
                showSearch = showSearch,
                folderDisplaySettingsUseCase = context.displaySettingsUseCase,
                getFileUseCase = context.getFileUseCase,
                lazyGridState = lazyGridState,
                snackbarHostState = snackbarHostState,
                lazyPagingItems = lazyPagingItems,
                scope = coroutineScope,
            )
        },
    ) {
        FolderScreenStateImpl(
            bookshelfId = bookshelfId,
            path = path,
            restorePath = restorePath,
            showSearch = showSearch,
            folderDisplaySettingsUseCase = context.displaySettingsUseCase,
            getFileUseCase = context.getFileUseCase,
            lazyGridState = lazyGridState,
            snackbarHostState = snackbarHostState,
            lazyPagingItems = lazyPagingItems,
            scope = coroutineScope,
        )
    }.apply {
        scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    }

    LaunchedEffect(state) {
        snapshotFlow { state.lazyPagingItems }.collect {
            state.onLoadStateChange(state.lazyPagingItems)
        }
    }

    return state
}

private class FolderScreenStateImpl(
    private val bookshelfId: BookshelfId,
    private val path: String,
    private val restorePath: String?,
    showSearch: Boolean,
    override val lazyGridState: LazyGridState,
    override val snackbarHostState: SnackbarHostState,
    override val lazyPagingItems: LazyPagingItems<File>,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    var scope: CoroutineScope,
    getFileUseCase: GetFileUseCase,
) : FolderScreenState {
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState

    override val events = EventFlow<FolderScreenEvent>()
    override var uiState by mutableStateOf(
        FolderScreenUiState(
            folderAppBarUiState = FolderAppBarUiState(showSearch = showSearch),
            folderListUiState = FolderListUiState(emphasisPath = restorePath.orEmpty()),
        ),
    )
        private set

    var isRestored by mutableStateOf(false)

    init {
        folderDisplaySettingsUseCase.settings
            .distinctUntilChanged()
            .onEach { folderDisplaySettings ->
                uiState = uiState.copy(
                    folderAppBarUiState = uiState.folderAppBarUiState.copy(
                        folderScopeOnly = folderDisplaySettings.folderScopeOnlyList.any { scopeOnly, ->
                            scopeOnly.bookshelfId == bookshelfId && scopeOnly.path == path
                        },
                        sortType = folderDisplaySettings.folderScopeOnlyList
                            .find { scopeOnly ->
                                scopeOnly.bookshelfId == bookshelfId &&
                                    scopeOnly.path == path
                            }?.sortType
                            ?: folderDisplaySettings.sortType,
                    ),
                    folderListUiState = uiState.folderListUiState.copy(
                        fileLazyVerticalGridUiState = uiState.folderListUiState.fileLazyVerticalGridUiState
                            .copy(
                                fileListDisplay = folderDisplaySettings.fileListDisplay,
                                columnSize = folderDisplaySettings.gridColumnSize,
                                imageScale = folderDisplaySettings.imageScale,
                                imageFilterQuality = folderDisplaySettings.imageFilterQuality,
                                fontSize = folderDisplaySettings.fontSize,
                                showThumbnails = folderDisplaySettings.showThumbnails,
                            ),
                    ),
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

    override fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>) {
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
                scope.launch {
                    when (it) {
                        is PagingException.InvalidAuth -> snackbarHostState.showSnackbar(
                            "認証エラー",
                        )

                        is PagingException.InvalidServer -> snackbarHostState.showSnackbar(
                            "サーバーエラー",
                        )

                        is PagingException.NoNetwork -> snackbarHostState.showSnackbar(
                            "ネットワークエラー",
                        )

                        is PagingException.NotFound -> snackbarHostState.showSnackbar(
                            "見つかりませんでした",
                        )
                    }
                }
            }
        }
    }

    override fun onSortClick(sortType: SortType) {
        scope.launch {
            var refresh = false
            folderDisplaySettingsUseCase.edit { settings ->
                val beforeFolderScopeOnly =
                    settings.folderScopeOnlyList.find {
                        it.bookshelfId == bookshelfId &&
                            it.path == path
                    }
                when {
                    uiState.folderAppBarUiState.folderScopeOnly -> {
                        if (beforeFolderScopeOnly == null) {
                            refresh = true
                            settings.copy(
                                folderScopeOnlyList =
                                settings.folderScopeOnlyList + FolderScopeOnly(
                                    bookshelfId,
                                    path,
                                    sortType,
                                ),
                            )
                        } else if (beforeFolderScopeOnly.sortType != sortType) {
                            refresh = true
                            val new = FolderScopeOnly(
                                bookshelfId,
                                path,
                                sortType,
                            )
                            settings.copy(
                                folderScopeOnlyList =
                                settings.folderScopeOnlyList - beforeFolderScopeOnly + new,
                            )
                        } else {
                            settings
                        }
                    }

                    !uiState.folderAppBarUiState.folderScopeOnly && beforeFolderScopeOnly != null -> {
                        refresh = true
                        settings.copy(
                            folderScopeOnlyList =
                            settings.folderScopeOnlyList - beforeFolderScopeOnly,
                        )
                    }

                    settings.sortType != sortType -> {
                        refresh = true
                        settings.copy(sortType = sortType)
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

    override fun onFolderScopeOnlyClick() {
        scope.launch {
            folderDisplaySettingsUseCase.edit { settings ->
                val beforeFolderScopeOnly =
                    settings.folderScopeOnlyList.find {
                        it.bookshelfId == bookshelfId && it.path == path
                    }
                val folderScopeOnlyList = if (beforeFolderScopeOnly == null) {
                    settings.folderScopeOnlyList + FolderScopeOnly(
                        bookshelfId,
                        path,
                        settings.sortType,
                    )
                } else {
                    settings.folderScopeOnlyList - beforeFolderScopeOnly
                }
                settings.copy(folderScopeOnlyList = folderScopeOnlyList)
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

    override fun onRefresh() {
        lazyPagingItems.refresh()
    }

    private fun refreshItems() {
        lazyPagingItems.refresh()
    }

    companion object {
        fun saver(init: () -> FolderScreenStateImpl) = Saver<FolderScreenStateImpl, Boolean>(
            save = {
                it.isRestored
            },
            restore = {
                init().apply {
                    isRestored = it
                }
            },
        )
    }
}
