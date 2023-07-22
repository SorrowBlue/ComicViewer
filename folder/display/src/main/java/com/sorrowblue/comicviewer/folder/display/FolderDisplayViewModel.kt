package com.sorrowblue.comicviewer.folder.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.SortType
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal class FolderDisplayViewModel @Inject constructor(
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {

    fun update(display: FolderDisplaySettings.Display) {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit { it.copy(display = display) }
        }
    }

    fun update(size: FolderDisplaySettings.Size) {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit { it.copy(columnSize = size) }
        }
    }

    fun update(sortType: SortType) {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit { it.copy(sortType = sortType) }
        }
    }

    fun update(isAsc: Boolean) {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit { it.copy(sortType = it.sortType.copy2(isAsc)) }
        }
    }

    val displayFlow =
        manageFolderDisplaySettingsUseCase.settings.map { it.display }.distinctUntilChanged()
    val columnSizeFlow =
        manageFolderDisplaySettingsUseCase.settings.map { it.columnSize }.distinctUntilChanged()
    val sortTypeFlow =
        manageFolderDisplaySettingsUseCase.settings.map { it.sortType }.distinctUntilChanged()
    val isAscType =
        manageFolderDisplaySettingsUseCase.settings.map { it.sortType.isAsc }.distinctUntilChanged()
}
