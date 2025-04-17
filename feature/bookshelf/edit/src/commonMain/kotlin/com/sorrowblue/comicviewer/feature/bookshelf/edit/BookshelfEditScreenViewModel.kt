package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class BookshelfEditScreenViewModel(
    val getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    val registerBookshelfUseCase: RegisterBookshelfUseCase,
) : ViewModel()
