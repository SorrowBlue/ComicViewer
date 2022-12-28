package com.sorrowblue.comicviewer.bookshelf

import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.bookshelf.searchable.IBookshelfViewModel
import com.sorrowblue.comicviewer.domain.entity.ServerId
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.usecase.FullScanLibraryRequest
import com.sorrowblue.comicviewer.domain.usecase.FullScanLibraryUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfRequest
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.LoadFileRequest
import com.sorrowblue.comicviewer.domain.usecase.LoadFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.logcat

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    loadFileUseCase: LoadFileUseCase,
    private val manageBookshelfDisplaySettingsUseCase: ManageBookshelfDisplaySettingsUseCase,
    private val fullScanLibraryUseCase: FullScanLibraryUseCase,
    getServerBookshelfUseCase: GetServerBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : IBookshelfViewModel(manageBookshelfDisplaySettingsUseCase), SupportSafeArgs {

    private val args: BookshelfFragmentArgs by navArgs()

    init {
        logcat { "serverId=${args.serverId}, path=${args.path}" }
    }

    override var position = args.position
    override val transitionName = args.transitionName

    private val serverBookshelfFlow = getServerBookshelfUseCase.execute(
        GetServerBookshelfRequest(ServerId(args.serverId), Base64.decode(args.path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP).decodeToString())
    ).mapNotNull { it.dataOrNull }
    override val serverFlow = serverBookshelfFlow.map { it.server }.stateIn { null }
    private val bookshelfFlow = serverBookshelfFlow.map { it.bookshelf }.stateIn { null }

    override val titleFlow = serverFlow.mapNotNull { it?.displayName }.stateIn { "" }
    override val subTitleFlow = bookshelfFlow.mapNotNull { it?.name }.stateIn { "" }

    override val settings2 = runBlocking { manageBookshelfDisplaySettingsUseCase.settings.first() }
    override val settings =
        manageBookshelfDisplaySettingsUseCase.settings.shareIn(
            viewModelScope,
            SharingStarted.Eagerly,
            1
        )

    override val data = serverBookshelfFlow.flatMapLatest {
        loadFileUseCase.execute(LoadFileRequest(PagingConfig(100), it.server, it.bookshelf))
    }.cachedIn(viewModelScope)

    fun fullScan(scanType: ScanType, done: (String) -> Unit) {
        val bookshelf = bookshelfFlow.value ?: return
        viewModelScope.launch {
            fullScanLibraryUseCase.execute(FullScanLibraryRequest(bookshelf, scanType))
                .dataOrNull?.let { done.invoke(it) }
        }
    }
}
