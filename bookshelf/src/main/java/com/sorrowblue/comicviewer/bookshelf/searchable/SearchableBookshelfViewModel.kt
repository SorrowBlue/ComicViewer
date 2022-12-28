package com.sorrowblue.comicviewer.bookshelf.searchable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.PagingQueryFileRequest
import com.sorrowblue.comicviewer.domain.usecase.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking

@HiltViewModel
class SearchableBookshelfViewModel @Inject constructor(
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    private val manageBookshelfDisplaySettingsUseCase: ManageBookshelfDisplaySettingsUseCase,
    override val savedStateHandle: SavedStateHandle,
) : IBookshelfViewModel(manageBookshelfDisplaySettingsUseCase), SupportSafeArgs {

    private val args: SearchableBookshelfFragmentArgs by navArgs()
    override var position = 0
    override val transitionName: String? = null

    override val titleFlow: StateFlow<String> = MutableStateFlow("検索結果")
    override val subTitleFlow = MutableStateFlow(args.query)

    override val serverFlow: StateFlow<Server?> = MutableStateFlow(args.server)

    override val settings: SharedFlow<BookshelfDisplaySettings> =
        manageBookshelfDisplaySettingsUseCase.settings.shareIn(
            viewModelScope,
            SharingStarted.Eagerly,
            1
        )

    var query = args.query
    override val settings2 = runBlocking { manageBookshelfDisplaySettingsUseCase.settings.first() }

    override val data =
        pagingQueryFileUseCase.execute(
            PagingQueryFileRequest(
                PagingConfig(100),
                args.server
            ) { query })
            .cachedIn(viewModelScope)
}

abstract class IBookshelfViewModel(
    manageBookshelfDisplaySettingsUseCase: ManageBookshelfDisplaySettingsUseCase,
) : ViewModel() {


    abstract val transitionName: String?
    abstract val data: Flow<PagingData<File>>
    val spanCount = manageBookshelfDisplaySettingsUseCase.settings.map {
        when (it.display) {
            BookshelfDisplaySettings.Display.GRID -> it.spanCount
            BookshelfDisplaySettings.Display.LIST -> 1
        }
    }
    val isRefreshing = MutableStateFlow(false)
    abstract val settings: SharedFlow<BookshelfDisplaySettings>
    abstract val settings2: BookshelfDisplaySettings
    abstract var position: Int
    abstract val titleFlow: StateFlow<String>
    abstract val subTitleFlow: StateFlow<String>
    abstract val serverFlow: StateFlow<Server?>
}
