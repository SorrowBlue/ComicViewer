package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BookshelfInfoSheetViewModel @Inject constructor(
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase,
) : ViewModel()
