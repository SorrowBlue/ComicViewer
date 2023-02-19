package com.sorrowblue.comicviewer.library.onedrive.list

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
import com.sorrowblue.comicviewer.library.onedrive.data.AuthenticationProvider
import com.sorrowblue.comicviewer.library.onedrive.data.OneDriveApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
internal class OneDriveListViewModel @Inject constructor(
    private val provider: AuthenticationProvider,
    private val repository: OneDriveApiRepository,
    private val factory: OneDrivePagingSource.Factory,
    override val savedStateHandle: SavedStateHandle
) : PagingViewModel<File>(), SupportSafeArgs {

    private val args: OneDriveListFragmentArgs by navArgs()

    val currentUserFlow = repository.currentUserFlow.stateIn { null }

    val isAuthenticated = repository.isAuthenticated

    init {
        viewModelScope.launch {
            provider.initialize()
        }
    }

    override val transitionName = args.transitionName

    suspend fun profileImage() = repository.profileImage()
    fun signOut() {
        viewModelScope.launch {
            provider.signOut()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pagingDataFlow = currentUserFlow.filterNotNull().flatMapLatest {
        Pager(PagingConfig(20)) { factory.create(args.driveId, args.itemId.orEmpty()) }.flow
    }.cachedIn(viewModelScope)
}
