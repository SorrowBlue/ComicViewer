package com.sorrowblue.comicviewer.library.dropbox.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@HiltViewModel
internal class DropBoxListViewModel @Inject constructor(
    private val repository: DropBoxApiRepository,
    override val savedStateHandle: SavedStateHandle
) : PagingViewModel<File>(), SupportSafeArgs {

    private val args: DropBoxListFragmentArgs by navArgs()
    override val transitionName = args.transitionName
    override val pagingDataFlow = Pager(PagingConfig(20)) {
        DropBoxPagingSource(args.parent, repository)
    }.flow.cachedIn(viewModelScope)

    val account = repository.accountFlow.stateIn { null }

    fun isAuthenticated(): Flow<Boolean> {
        return repository.isAuthenticated().flowOn(Dispatchers.IO)
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}
