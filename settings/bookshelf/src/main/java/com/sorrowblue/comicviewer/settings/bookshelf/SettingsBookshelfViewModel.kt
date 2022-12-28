package com.sorrowblue.comicviewer.settings.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsBookshelfViewModel @Inject constructor(
    private val manageBookshelfSettings2UseCase: ManageBookshelfSettingsUseCase
) : ViewModel() {
    fun updateShowPreview(newValue: Boolean) {
        viewModelScope.launch {
            manageBookshelfSettings2UseCase.edit {
                it.copy(showPreview = newValue)
            }
        }
    }

    fun updateResolveImageFolder(newValue: Boolean) {
        viewModelScope.launch {
            manageBookshelfSettings2UseCase.edit {
                it.copy(resolveImageFolder = newValue)
            }
        }
    }

    val settings = manageBookshelfSettings2UseCase.settings
}
