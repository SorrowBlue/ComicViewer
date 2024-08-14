package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.onError
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class BookshelfEditViewModel @Inject constructor(
    val registerBookshelfUseCase: RegisterBookshelfUseCase,
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
) : ViewModel() {

    suspend fun fetch(bookshelfId: BookshelfId): BookshelfFolder? {
        return getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId))
            .first().dataOrNull()
    }

    fun save(
        bookshelf: Bookshelf,
        path: String,
        onError: (RegisterBookshelfUseCase.Error) -> Unit,
        complete: () -> Unit,
    ) {
        viewModelScope.launch {
            registerBookshelfUseCase(RegisterBookshelfUseCase.Request(bookshelf, path))
                .first()
                .onError {
                    onError(it)
                    logcat { it.toString() }
                }.onSuccess {
                    complete()
                }
        }
    }
}
