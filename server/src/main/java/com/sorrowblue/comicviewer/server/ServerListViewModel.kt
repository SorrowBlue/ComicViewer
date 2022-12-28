package com.sorrowblue.comicviewer.server

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.usecase.LoadServerPagingDataUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
internal class ServerListViewModel @Inject constructor(
    loadServerPagingDataUseCase: LoadServerPagingDataUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {

    val isEmptyData = MutableStateFlow(false)

    val pagingDataFlow =
        loadServerPagingDataUseCase.execute(LoadServerPagingDataUseCase.RequestData(PagingConfig(10)))
}
