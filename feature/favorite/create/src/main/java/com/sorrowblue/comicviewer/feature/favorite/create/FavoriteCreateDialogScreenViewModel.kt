package com.sorrowblue.comicviewer.feature.favorite.create

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FavoriteCreateDialogScreenViewModel @Inject constructor(
    val createFavoriteUseCase: CreateFavoriteUseCase,
    val addFavoriteFileUseCase: AddFavoriteFileUseCase,
) : ViewModel()
