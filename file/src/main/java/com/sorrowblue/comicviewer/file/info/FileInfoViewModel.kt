package com.sorrowblue.comicviewer.file.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
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
        getFileUseCase.execute(
            GetFileUseCase.Request(
                BookshelfId(args.bookshelfId),
                args.path.decodeFromBase64()
            )
        )
            .map { it.dataOrNull }
            .stateIn { null }

    val bookFlow = fileFlow.map { it as? Book }.stateIn { null }

    fun addReadLater(done: () -> Unit) {
        val file = fileFlow.value ?: return
        val request = AddReadLaterUseCase.Request(file.bookshelfId, file.path)
        viewModelScope.launch {
            addReadLaterUseCase.execute(request).first().fold({
                done()
            }, {}, {})
        }
    }
}
