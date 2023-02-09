package com.sorrowblue.comicviewer.file.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.decodeBase64
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FileInfoViewModel @Inject constructor(
    getFileUseCase: GetFileUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    private val args: FileInfoFragmentArgs by navArgs()

    val fileFlow =
        getFileUseCase.execute(GetFileUseCase.Request(args.bookshelfId, args.path.decodeBase64()))
            .stateIn { null }

    fun addReadLater(file: File, done: () -> Unit) {
        val request = AddReadLaterUseCase.Request(file.bookshelfId, file.path)
        viewModelScope.launch {
            addReadLaterUseCase.execute(request).first().fold({
                done()
            }, {}, {})
        }
    }

    private val FileInfoFragmentArgs.bookshelfId get() = BookshelfId(serverIdValue)
}
