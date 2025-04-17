package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class BookshelfInfoSheetViewModel(
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
) : ViewModel()
