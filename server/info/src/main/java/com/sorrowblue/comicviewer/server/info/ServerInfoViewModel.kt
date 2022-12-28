package com.sorrowblue.comicviewer.server.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoRequest
import com.sorrowblue.comicviewer.domain.usecase.ServerBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
internal class ServerInfoViewModel @Inject constructor(
    serverBookshelfUseCase: ServerBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerInfoFragmentArgs by navArgs()

    init {
        viewModelScope.launch {
            serverBookshelfUseCase.execute(GetLibraryInfoRequest(args.serverId))
        }
    }

    val libraryInfoFlow = serverBookshelfUseCase.source.mapNotNull {
        when (it) {
            is Result.Error -> {
                // TODO(Send error message)
                null
            }
            is Result.Exception -> {
                // TODO(Send system error message)
                null
            }
            is Result.Success -> it.data
        }
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val server: StateFlow<Server?> =
        libraryInfoFlow.map { it.server }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val bookshelf: StateFlow<Bookshelf?> =
        libraryInfoFlow.map { it.bookshelf }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
