package com.sorrowblue.comicviewer.feature.bookshelf.remove

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BookshelfRemoveDialogViewModel @Inject constructor(
    val removeBookshelfUseCase: RemoveBookshelfUseCase,
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase,
) : ViewModel()
