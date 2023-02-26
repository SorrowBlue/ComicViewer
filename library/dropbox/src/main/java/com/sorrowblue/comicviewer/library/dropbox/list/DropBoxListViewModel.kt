package com.sorrowblue.comicviewer.library.dropbox.list

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
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

internal class DropBoxListViewModel(
    application: Application,
    override val savedStateHandle: SavedStateHandle
) : PagingAndroidViewModel<File>(application), SupportSafeArgs {

    private val repository = DropBoxApiRepository.getInstance(application)

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
