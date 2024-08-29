package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BookshelfEditScreenViewModel @Inject constructor(
    val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    val registerBookshelfUseCase: RegisterBookshelfUseCase,
) : ViewModel()
