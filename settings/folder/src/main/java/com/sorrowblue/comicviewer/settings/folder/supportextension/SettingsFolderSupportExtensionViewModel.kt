package com.sorrowblue.comicviewer.settings.folder.supportextension

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsFolderSupportExtensionViewModel @Inject constructor(
    application: Application,
    private val settingsUseCase: ManageFolderSettingsUseCase
) : AndroidViewModel(application) {

    val supportExtension = settingsUseCase.settings.map { it.supportExtension }


    fun removeExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit {
                val list = it.supportExtension.toMutableSet()
                list.remove(extension)
                it.copy(supportExtension = list.sortedBy(SupportExtension::extension).toSet())
            }
        }
    }

    fun addExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit {
                val list = it.supportExtension.toMutableSet()
                list.add(extension)
                it.copy(supportExtension = list.sortedBy(SupportExtension::extension).toSet())
            }
        }
    }
}
