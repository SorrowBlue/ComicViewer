package com.sorrowblue.comicviewer.settings.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsBookshelfSupportExtensionViewModel @Inject constructor(
    private val settingsUseCase: ManageFolderSettingsUseCase
) : ViewModel() {

    val supportExtension =
        settingsUseCase.settings.map { it.supportExtension }
            .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun removeExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit {
                val list = it.supportExtension.toMutableSet()
                list.remove(extension)
                it.copy(supportExtension = list.sorted().toSet())
            }
        }
    }

    fun addExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit {
                val list = it.supportExtension.toMutableSet()
                list.add(extension)
                it.copy(supportExtension = list.sorted().toSet())
            }
        }
    }
}
