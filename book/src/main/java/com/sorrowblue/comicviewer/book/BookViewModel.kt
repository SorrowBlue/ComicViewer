package com.sorrowblue.comicviewer.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.sorrowblue.comicviewer.domain.entity.ServerId
import com.sorrowblue.comicviewer.domain.model.BindingDirection
import com.sorrowblue.comicviewer.domain.model.History
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.UpdateHistoryRequest
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryFileResult
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRequest
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookRequest
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateLastReadPageRequest
import com.sorrowblue.comicviewer.domain.usecase.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ViewerSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
internal class BookViewModel @Inject constructor(
    private val updateHistoryUseCase: UpdateHistoryUseCase,
    getLibraryFileUseCase: GetServerBookUseCase,
    private val getNextComicUseCase: GetNextComicUseCase,
    private val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
    viewerSettingsUseCase: ViewerSettingsUseCase,
    viewerOperationSettingsUseCase: ViewerOperationSettingsUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookFragmentArgs by navArgs()
    val placeholder = args.placeholder

    val libraryComic =
        getLibraryFileUseCase.execute(GetServerBookRequest(ServerId(args.serverId), args.path))
            .mapNotNull {
                when (it) {
                    is Result.Error -> {
                        when (it.error) {
                            GetLibraryFileResult.NO_LIBRARY -> state.value = LoadingState.NOT_FOUND
                            GetLibraryFileResult.NO_FILE -> state.value = LoadingState.NOT_FOUND
                        }
                        null
                    }
                    is Result.Exception -> {
                        state.value = LoadingState.ERROR
                        null
                    }
                    is Result.Success -> {
                        state.value = LoadingState.SUCCESS
                        it.data
                    }
                }
            }.stateIn { null }
    val serverFlow =
        libraryComic.mapNotNull { it?.server }.stateIn { null }
    val bookFlow =
        libraryComic.mapNotNull { it?.book }.stateIn { null }
    val nextComic = bookFlow.filterNotNull().mapNotNull {
        val result = getNextComicUseCase.execute(
            GetNextComicRequest(
                it.serverId,
                it.path,
                GetNextComicRel.NEXT
            )
        )
        when (result) {
            is Result.Error -> null
            is Result.Exception -> null
            is Result.Success -> result.data
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val prevComic = bookFlow.filterNotNull().mapNotNull {
        val result = getNextComicUseCase.execute(
            GetNextComicRequest(
                it.serverId,
                it.path,
                GetNextComicRel.PREV
            )
        )
        when (result) {
            is Result.Error -> null
            is Result.Exception -> null
            is Result.Success -> result.data
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val layoutDirectionFlow =
        viewerOperationSettingsUseCase.settings.distinctUntilChangedBy { it.bindingDirection }
            .map {
                when (it.bindingDirection) {
                    BindingDirection.LTR -> ViewPager2.LAYOUT_DIRECTION_LTR
                    BindingDirection.RTL -> ViewPager2.LAYOUT_DIRECTION_RTL
                }
            }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val readAheadPageCountFlow = viewerSettingsUseCase.settings.map { it.readAheadPageCount }
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)


    val state = MutableStateFlow(LoadingState.LOADING)

    val transitionName = args.transitionName
    val isVisibleUI = MutableStateFlow(false)
    val viewerSettings =
        viewerSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)


    val title = bookFlow.mapNotNull { it?.name }.stateIn { "" }

    val pageCount = bookFlow.mapNotNull { it?.totalPageCount }.stateIn { 1 }

    val pageIndex = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            bookFlow.filterNotNull().collectLatest {
                updateHistoryUseCase.execute(
                    UpdateHistoryRequest(
                        History(
                            ServerId(args.serverId), it.parent, args.position
                        )
                    )
                )
            }
        }
    }

    fun updateLastReadPage(index: Int) {
        if (serverFlow.value == null || bookFlow.value == null) return
        val request = UpdateLastReadPageRequest(serverFlow.value!!.id, bookFlow.value!!.path, index)
        viewModelScope.launch {
            updateLastReadPageUseCase.execute(request)
        }
    }
}
