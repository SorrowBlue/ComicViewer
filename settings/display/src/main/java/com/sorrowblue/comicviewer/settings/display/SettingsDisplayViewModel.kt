package com.sorrowblue.comicviewer.settings.display

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.DarkMode
import com.sorrowblue.comicviewer.domain.model.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.usecase.settings.DisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsDisplayViewModel @Inject constructor(
    private val settingsUseCase: DisplaySettingsUseCase
) : ViewModel() {

    val darkMode = settingsUseCase.settings.map { it.darkMode }.stateIn { null }

    val folderThumbnailOrder =
        settingsUseCase.settings.map { it.folderThumbnailOrder }.stateIn { null }

    fun updateDarkMode(newDarkMode: DarkMode) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(darkMode = newDarkMode) }
            when (newDarkMode) {
                DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }.let(AppCompatDelegate::setDefaultNightMode)
        }
    }

    fun updateFolderThumbnailOrder(folderThumbnailOrder: FolderThumbnailOrder) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(folderThumbnailOrder = folderThumbnailOrder) }
        }
    }
}
