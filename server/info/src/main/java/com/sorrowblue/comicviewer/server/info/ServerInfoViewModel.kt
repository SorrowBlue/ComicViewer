package com.sorrowblue.comicviewer.server.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.usecase.GetServerInfoUseCase
import com.sorrowblue.comicviewer.framework.Result
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

@HiltViewModel
internal class ServerInfoViewModel @Inject constructor(
    getServerInfoUseCase: GetServerInfoUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: ServerInfoFragmentArgs by navArgs()

    private val libraryInfoFlow =
        getServerInfoUseCase.execute(GetServerInfoUseCase.Request(args.serverId)).mapNotNull {
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
    val folder: StateFlow<Folder?> =
        libraryInfoFlow.map { it.bookshelf }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
