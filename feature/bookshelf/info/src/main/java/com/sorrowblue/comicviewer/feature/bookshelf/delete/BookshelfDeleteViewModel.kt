package com.sorrowblue.comicviewer.feature.bookshelf.delete

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class BookshelfDeleteViewModel(
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
) : ViewModel()
