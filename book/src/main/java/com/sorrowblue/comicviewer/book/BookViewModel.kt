package com.sorrowblue.comicviewer.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.viewpager2.widget.ViewPager2
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.entity.settings.BindingDirection
import com.sorrowblue.comicviewer.domain.entity.settings.History
import com.sorrowblue.comicviewer.domain.usecase.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.decodeBase64
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
internal class BookViewModel @Inject constructor(
    getBookUseCase: GetBookUseCase,
    private val updateHistoryUseCase: UpdateHistoryUseCase,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
    manageViewerOperationSettingsUseCase: ManageViewerOperationSettingsUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookFragmentArgs by navArgs()

    val placeholder = args.placeholder

    val bookFlow =
        getBookUseCase.execute(GetBookUseCase.Request(ServerId(args.serverId), args.path.decodeBase64()))
            .map { it.dataOrNull }
            .stateIn { null }

    val nextComic = bookFlow.filterNotNull().distinctUntilChangedBy { it.path }.flatMapLatest {
        getNextBookUseCase
            .execute(GetNextBookUseCase.Request(it.serverId, it.path, GetNextComicRel.NEXT))
            .map { it.dataOrNull }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val prevComic = bookFlow.filterNotNull().distinctUntilChangedBy { it.path }.flatMapLatest {
        getNextBookUseCase
            .execute(GetNextBookUseCase.Request(it.serverId, it.path, GetNextComicRel.PREV))
            .map { it.dataOrNull }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val layoutDirectionFlow =
        manageViewerOperationSettingsUseCase.settings.distinctUntilChangedBy { it.bindingDirection }
            .map {
                when (it.bindingDirection) {
                    BindingDirection.LTR -> ViewPager2.LAYOUT_DIRECTION_LTR
                    BindingDirection.RTL -> ViewPager2.LAYOUT_DIRECTION_RTL
                }
            }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val readAheadPageCountFlow = manageViewerSettingsUseCase.settings.map { it.readAheadPageCount }
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)


    val state = MutableStateFlow(LoadingState.LOADING)

    val transitionName = args.transitionName
    val isVisibleUI = MutableStateFlow(false)
    val viewerSettings =
        manageViewerSettingsUseCase.settings.shareIn(viewModelScope, SharingStarted.Eagerly, 1)


    val title = bookFlow.mapNotNull { it?.name }.stateIn { "" }

    val pageCount = bookFlow.mapNotNull { it?.totalPageCount }.stateIn { 1 }

    val pageIndex = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            bookFlow.filterNotNull().collectLatest {
                updateHistoryUseCase.execute(
                    UpdateHistoryUseCase.Request(
                        History(ServerId(args.serverId), it.parent, args.position)
                    )
                )
            }
        }
    }

    fun updateLastReadPage(index: Int) {
        val book = bookFlow.replayCache.firstOrNull() ?: return
        val request = UpdateLastReadPageUseCase.Request(book.serverId, book.path, index)
        viewModelScope.launch {
            updateLastReadPageUseCase.execute(request)
        }
    }
}
