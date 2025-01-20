package com.sorrowblue.comicviewer.feature.book.menu

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class BookMenuViewModel(
    val manageBookSettingsUseCase: ManageBookSettingsUseCase,
) : ViewModel()
