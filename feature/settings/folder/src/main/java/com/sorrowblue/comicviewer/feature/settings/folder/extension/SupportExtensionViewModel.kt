package com.sorrowblue.comicviewer.feature.settings.folder.extension

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
internal class SupportExtensionViewModel @Inject constructor(
    application: Application,
    private val settingsUseCase: ManageFolderSettingsUseCase,
) : AndroidViewModel(application) {

    val settingsFlow = settingsUseCase.settings.map { it.supportExtension }

    fun toggleExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit { settings ->
                val newValue = if (extension in settings.supportExtension) {
                    settings.supportExtension.filterNot { it == extension }
                } else {
                    settings.supportExtension + extension
                }
                settings.copy(
                    supportExtension = newValue.sortedBy(SupportExtension::extension).toSet()
                )
            }
        }
    }
}
