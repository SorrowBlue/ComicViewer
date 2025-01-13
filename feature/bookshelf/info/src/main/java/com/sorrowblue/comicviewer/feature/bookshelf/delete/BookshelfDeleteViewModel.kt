package com.sorrowblue.comicviewer.feature.bookshelf.delete

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BookshelfDeleteViewModel @Inject constructor(
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
) : ViewModel()
