package com.sorrowblue.comicviewer.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.file.list.FileListViewModel
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteViewModel @Inject constructor(
    getFavoriteUseCase: GetFavoriteUseCase,
    manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    override val savedStateHandle: SavedStateHandle
) : FileListViewModel(manageFolderDisplaySettingsUseCase), SupportSafeArgs {

    private val args: FavoriteFragmentArgs by navArgs()

    val favoriteId = FavoriteId(args.favoriteId)

    private val favoriteFlow = getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
        .mapNotNull { it.dataOrNull }

    override val transitionName = args.transitionName

    override val pagingDataFlow = pagingFavoriteFileUseCase
        .execute(PagingFavoriteFileUseCase.Request(PagingConfig(20), favoriteId))
        .cachedIn(viewModelScope)

    val titleFlow = favoriteFlow.map { it.name }.stateIn { "" }

    val countFlow = favoriteFlow.map { it.count }.stateIn { 0 }

    fun delete(done: () -> Unit) {
        viewModelScope.launch {
            deleteFavoriteUseCase.execute(DeleteFavoriteUseCase.Request(FavoriteId(args.favoriteId)))
                .collect()
            done()
        }
    }
}
