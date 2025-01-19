package com.sorrowblue.comicviewer.feature.book

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class BookViewModel(
    val getBookUseCase: GetBookUseCase,
    val getNextBookUseCase: GetNextBookUseCase,
    val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
    val manageBookSettingsUseCase: ManageBookSettingsUseCase,
) : ViewModel()
