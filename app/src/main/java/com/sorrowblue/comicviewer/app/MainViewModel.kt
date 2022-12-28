package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.EmptyRequest
import com.sorrowblue.comicviewer.domain.usecase.GetHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    var shouldKeepOnScreen = true

    val settings = loadSettingsUseCase.settings

    val navigationHistory =
        MutableSharedFlow<Triple<Server?, List<Bookshelf>, Int>>(1, 1, BufferOverflow.DROP_OLDEST)

    init {

        viewModelScope.launch {
            getHistoryUseCase.execute(EmptyRequest).onError {
                navigationHistory.emit(Triple(null, emptyList(), 0))
            }.onSuccess {
                navigationHistory.emit(it)
            }
        }
    }
}
