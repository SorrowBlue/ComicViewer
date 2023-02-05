package com.sorrowblue.comicviewer.readlater

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class ReadLaterViewModel @Inject constructor(
    manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    pagingReadLaterUseCase: PagingReadLaterUseCase,
) : PagingViewModel<File>() {

    override val transitionName = null
    override val pagingDataFlow = pagingReadLaterUseCase
        .execute(PagingReadLaterUseCase.Request(PagingConfig(20)))
        .cachedIn(viewModelScope)

    val folderDisplaySettingsFlow = manageFolderDisplaySettingsUseCase.settings
    val spanCountFlow = folderDisplaySettingsFlow.map { it.rawSpanCount }
        .stateIn { runBlocking { folderDisplaySettingsFlow.first().rawSpanCount } }

    private val FolderDisplaySettings.rawSpanCount
        get() = when (display) {
            FolderDisplaySettings.Display.GRID -> spanCount
            FolderDisplaySettings.Display.LIST -> 1
        }
}
