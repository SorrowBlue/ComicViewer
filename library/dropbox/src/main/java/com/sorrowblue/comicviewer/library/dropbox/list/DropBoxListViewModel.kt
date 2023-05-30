package com.sorrowblue.comicviewer.library.dropbox.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepository
import com.sorrowblue.comicviewer.library.filelist.LibraryFileListViewModel

internal class DropBoxListViewModel(
    private val repository: DropBoxApiRepository,
    override val savedStateHandle: SavedStateHandle
) : LibraryFileListViewModel(), SupportSafeArgs {

    companion object {

        fun Factory(repository: DropBoxApiRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()
                return DropBoxListViewModel(repository, savedStateHandle) as T
            }
        }
    }

    private val args: DropBoxListFragmentArgs by navArgs()
    override val transitionName = args.transitionName
    override val pagingDataFlow = Pager(PagingConfig(20)) {
        DropBoxPagingSource(args.parent, repository)
    }.flow.cachedIn(viewModelScope)

    override val isAuthenticated = repository.isAuthenticated

    val account = repository.accountFlow.stateIn { null }
}
