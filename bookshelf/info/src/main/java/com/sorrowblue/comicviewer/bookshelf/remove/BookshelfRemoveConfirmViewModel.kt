package com.sorrowblue.comicviewer.bookshelf.remove

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookshelfRemoveConfirmViewModel @Inject constructor(
    private val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookshelfRemoveConfirmDialogArgs by navArgs()

    fun remove(done: () -> Unit) {
        viewModelScope.launch {
            val bookshelf =
                getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
                    .first().dataOrNull?.bookshelf
            if (bookshelf != null) {
                removeBookshelfUseCase.execute(RemoveBookshelfUseCase.Request(bookshelf))
                done()
            }
        }
    }
}