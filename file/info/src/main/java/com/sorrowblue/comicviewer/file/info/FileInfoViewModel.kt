package com.sorrowblue.comicviewer.file.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.decodeBase64
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal class FileInfoViewModel @Inject constructor(
    getFileUseCase: GetFileUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    private val args: FileInfoFragmentArgs by navArgs()

    val fileFlow =
        getFileUseCase.execute(GetFileUseCase.Request(args.serverId, args.path.decodeBase64()))
            .map { it.dataOrNull }
            .stateIn { null }

    val bookFlow = fileFlow.map { it as? Book }.stateIn { null }

    fun addReadLater(done: () -> Unit) {
        val file = fileFlow.value ?: return
        val request = AddReadLaterUseCase.Request(file.serverId, file.path)
        viewModelScope.launch {
            addReadLaterUseCase.execute(request).first().fold({
                done()
            }, {}, {})
        }
    }

    private val FileInfoFragmentArgs.serverId get() = ServerId(serverIdValue)
}
