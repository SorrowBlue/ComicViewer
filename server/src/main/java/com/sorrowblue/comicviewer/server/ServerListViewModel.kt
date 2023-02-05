package com.sorrowblue.comicviewer.server

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingServerUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ServerListViewModel @Inject constructor(
    pagingServerUseCase: PagingServerUseCase,
    override val savedStateHandle: SavedStateHandle
) : PagingViewModel<ServerFolder>(), SupportSafeArgs {

    override val transitionName = null
    override val pagingDataFlow =
        pagingServerUseCase.execute(PagingServerUseCase.Request(PagingConfig(10)))
            .cachedIn(viewModelScope)
}
