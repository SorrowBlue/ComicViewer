package com.sorrowblue.comicviewer.bookshelf.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
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
internal class BookshelfInfoViewModel @Inject constructor(
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookshelfInfoFragmentArgs by navArgs()

    private val libraryInfoFlow =
        getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
            .mapNotNull {
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

    val bookshelf: StateFlow<Bookshelf?> =
        libraryInfoFlow.map { it.bookshelf }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val folder: StateFlow<Folder?> =
        libraryInfoFlow.map { it.folder }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
