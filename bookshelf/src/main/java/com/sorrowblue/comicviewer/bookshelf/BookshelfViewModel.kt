package com.sorrowblue.comicviewer.bookshelf

import android.util.Base64
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.usecase.FullScanLibraryUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import logcat.logcat

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookshelfViewModel @Inject constructor(
    pagingFileUseCase: PagingFileUseCase,
    pagingQueryFileUseCase: PagingQueryFileUseCase,
    private val manageBookshelfDisplaySettingsUseCase: ManageBookshelfDisplaySettingsUseCase,
    private val fullScanLibraryUseCase: FullScanLibraryUseCase,
    getServerBookshelfUseCase: GetServerBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : AbstractBookshelfViewModel(manageBookshelfDisplaySettingsUseCase), SupportSafeArgs {

    private val args: BookshelfFragmentArgs by navArgs()

    override var position = args.position

    override val transitionName = args.transitionName

    private val serverBookshelfFlow = getServerBookshelfUseCase.source.mapNotNull { it.dataOrNull }

    val serverFlow = serverBookshelfFlow.map { it.server }.stateIn { null }

    private val bookshelfFlow = serverBookshelfFlow.map { it.bookshelf }.stateIn { null }

    override val titleFlow = serverFlow.mapNotNull { it?.displayName }.stateIn { "" }

    override val subTitleFlow = bookshelfFlow.mapNotNull { it?.name }.stateIn { "" }

    override val pagingDataFlow = serverBookshelfFlow.flatMapLatest {
        pagingFileUseCase.execute(
            PagingFileUseCase.Request(PagingConfig(100), it.server, it.bookshelf)
        )
    }.cachedIn(viewModelScope)

    override val pagingQueryDataFlow = serverBookshelfFlow.flatMapLatest {
        pagingQueryFileUseCase.execute(
            PagingQueryFileUseCase.Request(PagingConfig(100), it.server) {
                query
            }
        )
    }.cachedIn(viewModelScope)

    init {
        val serverId = ServerId(args.serverId)
        val path = Base64.decode(args.path.encodeToByteArray(), Base64.URL_SAFE or Base64.NO_WRAP)
            .decodeToString()
        viewModelScope.launch {
            getServerBookshelfUseCase.execute(GetServerBookshelfUseCase.Request(serverId, path))
        }
    }

    fun fullScan(scanType: ScanType, done: (String) -> Unit) {
        val bookshelf = bookshelfFlow.value ?: return
        viewModelScope.launch {
            fullScanLibraryUseCase.execute(
                FullScanLibraryUseCase.Request(
                    bookshelf, scanType
                )
            ).first().dataOrNull?.let { done.invoke(it) }
        }
    }
}
