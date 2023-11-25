package com.sorrowblue.comicviewer.feature.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.History
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetNextFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.feature.book.navigation.BookArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@HiltViewModel
internal class BookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBookUseCase: GetBookUseCase,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val getNextFavoriteBookUseCase: GetNextFavoriteBookUseCase,
    private val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
    private val updateHistoryUseCase: UpdateHistoryUseCase,
    private val manageBookSettingsUseCase: ManageBookSettingsUseCase,
) : ViewModel() {

    private val args = BookArgs(savedStateHandle)

    val bookSettings = manageBookSettingsUseCase.settings

    suspend fun updateBookSettings(action: (BookSettings) -> BookSettings) {
        manageBookSettingsUseCase.edit(action)
    }

    internal suspend fun nextBook(rel: GetNextComicRel): Book? {
        return if (0 < args.favoriteId.value) {
            getNextFavoriteBookUseCase.execute(
                GetNextFavoriteBookUseCase.Request(
                    FavoriteFile(
                        args.favoriteId,
                        args.bookshelfId,
                        args.path
                    ),
                    rel
                )
            ).first().dataOrNull
        } else {
            getNextBookUseCase
                .execute(GetNextBookUseCase.Request(args.bookshelfId, args.path, rel))
                .first().dataOrNull
        }
    }

    suspend fun updateLastReadPage(index: Int) {
        val request = UpdateLastReadPageUseCase.Request(args.bookshelfId, args.path, index)
        updateLastReadPageUseCase.execute(request)
    }

    suspend fun getBook(id: BookshelfId, path: String): Book? {
        return getBookUseCase.execute(GetBookUseCase.Request(id, path))
            .first().dataOrNull
    }

    suspend fun updateHistory(history: History) {
        updateHistoryUseCase.execute(
            UpdateHistoryUseCase.Request(history)
        )
    }
}
