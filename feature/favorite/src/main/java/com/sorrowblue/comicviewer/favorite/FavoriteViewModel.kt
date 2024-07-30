package com.sorrowblue.comicviewer.favorite

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FavoriteViewModel @Inject constructor(
    val getFavoriteUseCase: GetFavoriteUseCase,
    val pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    val addReadLaterUseCase: AddReadLaterUseCase,
    val getFileAttributeUseCase: GetFileAttributeUseCase,
    val existsReadlaterUseCase: ExistsReadlaterUseCase,
    val deleteReadLaterUseCase: DeleteReadLaterUseCase,
) : ViewModel()
