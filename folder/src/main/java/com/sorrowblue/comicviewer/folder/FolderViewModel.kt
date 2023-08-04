package com.sorrowblue.comicviewer.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.entity.SearchCondition
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.framework.Resource
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

enum class SearchRange {
    BOOKSHELF, IN_FOLDER, FOLDER_BELOW
}

enum class SearchPeriod {
    NONE,
    HOUR_24,
    WEEK_1,
    MONTH_1
}

enum class SearchOrder {
    NAME, TIMESTAMP, SIZE
}

enum class SearchSort {
    ASC, DESC
}

@HiltViewModel
internal class FolderViewModel @Inject constructor(
    getFileUseCase: GetFileUseCase,
    pagingFileUseCase: PagingFileUseCase,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: FolderFragmentArgs by navArgs()
    private val bookshelfId = BookshelfId(args.bookshelfId)
    private val path = args.path.decodeFromBase64()

    val name = getFileUseCase.execute(GetFileUseCase.Request(bookshelfId, path)).filterSuccess()
        .map { it.name }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = pagingFileUseCase.execute(
        PagingFileUseCase.Request(PagingConfig(30), bookshelfId, path)
    ).filterSuccess().flattenConcat().cachedIn(viewModelScope)


    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    fun updateQuery(query: String) {
        _query.value = query
    }

    private val _searchRange = MutableStateFlow(SearchRange.BOOKSHELF)
    val searchRange = _searchRange.asStateFlow()
    fun updateSearchRange(range: SearchRange) {
        _searchRange.value = range
    }

    private val _searchPeriod = MutableStateFlow(SearchPeriod.NONE)
    val searchPeriod = _searchPeriod.asStateFlow()
    fun updateSearchPeriod(searchPeriod: SearchPeriod) {
        _searchPeriod.value = searchPeriod
    }

    private val _searchOrder = MutableStateFlow(SearchOrder.NAME)
    val searchOrder = _searchOrder.asStateFlow()
    fun updateSearchOrder(searchOrder: SearchOrder) {
        _searchOrder.value = searchOrder
    }

    private val _searchSort = MutableStateFlow(SearchSort.ASC)
    val searchSort = _searchSort.asStateFlow()
    fun updateSearchSort(searchSort: SearchSort) {
        _searchSort.value = searchSort
    }

    val searchPagingDataFlow = pagingQueryFileUseCase.execute(
        PagingQueryFileUseCase.Request(PagingConfig(100), bookshelfId) {
            SearchCondition(
                query.value,
                when (searchRange.value) {
                    SearchRange.BOOKSHELF -> SearchCondition.Range.BOOKSHELF
                    SearchRange.IN_FOLDER -> SearchCondition.Range.InFolder(path)
                    SearchRange.FOLDER_BELOW -> SearchCondition.Range.FolderBelow(args.path)
                },
                when (searchPeriod.value) {
                    SearchPeriod.NONE -> SearchCondition.Period.NONE
                    SearchPeriod.HOUR_24 -> SearchCondition.Period.HOUR_24
                    SearchPeriod.WEEK_1 -> SearchCondition.Period.WEEK_1
                    SearchPeriod.MONTH_1 -> SearchCondition.Period.MONTH_1
                },
                when (searchOrder.value) {
                    SearchOrder.NAME -> SearchCondition.Order.NAME
                    SearchOrder.TIMESTAMP -> SearchCondition.Order.DATE
                    SearchOrder.SIZE -> SearchCondition.Order.SIZE
                },
                when (searchSort.value) {
                    SearchSort.ASC -> SearchCondition.Sort.ASC
                    SearchSort.DESC -> SearchCondition.Sort.DESC
                }
            )
        }
    ).cachedIn(viewModelScope)

    fun add(file: File) {
        viewModelScope.launch {
            addReadLaterUseCase.execute(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                .first()
        }
    }
}

private fun <T, E : Resource.AppError> Flow<Resource<T, E>>.filterSuccess(): Flow<T> {
    return filter {
        logcat { "filter" }
        it is Resource.Success<T>
    }.map {
        logcat { "map" }
        (it as Resource.Success<T>).data
    }
}
