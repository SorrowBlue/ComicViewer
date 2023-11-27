package com.sorrowblue.comicviewer.feature.book

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BookViewModel @Inject constructor(
    val getBookUseCase: GetBookUseCase,
    val getNextBookUseCase: GetNextBookUseCase,
    val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
    val manageBookSettingsUseCase: ManageBookSettingsUseCase,
) : ViewModel()
