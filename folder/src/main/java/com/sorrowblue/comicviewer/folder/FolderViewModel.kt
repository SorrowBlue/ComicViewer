package com.sorrowblue.comicviewer.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.entity.SearchCondition
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.SortType
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.file.list.FileListViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@HiltViewModel
internal class FolderViewModel @Inject constructor(
    pagingFileUseCase: PagingFileUseCase,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    getBookshelfFolderUseCase: GetBookshelfFolderUseCase,
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : FileListViewModel(manageFolderDisplaySettingsUseCase), SupportSafeArgs {

    private val args: FolderFragmentArgs by navArgs()

    override val transitionName = args.transitionName

    private val serverFolderFlow = getBookshelfFolderUseCase.execute(
        GetBookshelfFolderUseCase.Request(BookshelfId(args.serverId), args.path.decodeFromBase64())
    ).mapNotNull { it.dataOrNull }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(), 1)

    var position = args.position
    val titleFlow = serverFolderFlow.map { it.bookshelf.displayName }.stateIn { "" }
    val subTitleFlow = serverFolderFlow.map { it.folder.name }.stateIn { "" }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pagingDataFlow = serverFolderFlow.flatMapLatest {
        pagingFileUseCase.execute(
            PagingFileUseCase.Request(PagingConfig(100), it.bookshelf, it.folder)
        )
    }.cachedIn(viewModelScope)

    fun fullScan(scanType: ScanType, done: (String) -> Unit) {
        viewModelScope.launch {
            val folder = serverFolderFlow.firstOrNull()?.folder ?: return@launch
            scanBookshelfUseCase.execute(ScanBookshelfUseCase.Request(folder, scanType))
                .first().dataOrNull?.let { done.invoke(it) }
        }
    }

    val isEditing = MutableStateFlow(false)

    val searchQueryFlow = MutableStateFlow("")
    val searchRangeFlow = MutableStateFlow<SearchCondition.Range>(SearchCondition.Range.BOOKSHELF)
    val searchPeriodFlow = MutableStateFlow(SearchCondition.Period.NONE)
    val searchSortTypeFlow = MutableStateFlow<SortType>(SortType.NAME(true))

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchPagingDataFlow = combine(
        serverFolderFlow,
        searchQueryFlow,
        searchRangeFlow,
        searchPeriodFlow
    ) { bookshelfFolder, query, range, period ->
        bookshelfFolder to SearchCondition(
            query,
            when (range) {
                SearchCondition.Range.BOOKSHELF -> SearchCondition.Range.BOOKSHELF
                is SearchCondition.Range.FOLDER_BELOW ->
                    SearchCondition.Range.FOLDER_BELOW(bookshelfFolder.folder.path)

                is SearchCondition.Range.IN_FOLDER ->
                    SearchCondition.Range.IN_FOLDER(bookshelfFolder.folder.path)
            },
            period
        )
    }.flatMapLatest {
        pagingQueryFileUseCase.execute(
            PagingQueryFileUseCase.Request(
                PagingConfig(100),
                it.first.bookshelf,
                it.second
            ) { searchSortTypeFlow.value }
        )
    }.cachedIn(viewModelScope)
}
