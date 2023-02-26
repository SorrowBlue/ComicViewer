package com.sorrowblue.comicviewer.library.box.list

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingAndroidViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import com.sorrowblue.comicviewer.library.box.data.BoxApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

internal class BoxListViewModel(
    application: Application,
    override val savedStateHandle: SavedStateHandle
) : PagingAndroidViewModel<File>(application), SupportSafeArgs {

    private val repository = BoxApiRepository.getInstance(application)
    private val args: BoxListFragmentArgs by navArgs()
    override val transitionName = args.transitionName
    override val pagingDataFlow = Pager(PagingConfig(20)) {
        BoxPagingSource(args.parent, repository)
    }.flow.cachedIn(viewModelScope)

    val userInfoFlow = repository.userInfoFlow.stateIn { null }

    fun isAuthenticated(): Flow<Boolean> {
        return repository.isAuthenticated().flowOn(Dispatchers.IO)
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}
