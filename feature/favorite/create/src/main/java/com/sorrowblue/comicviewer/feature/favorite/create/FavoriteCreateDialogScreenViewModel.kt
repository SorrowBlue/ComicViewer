package com.sorrowblue.comicviewer.feature.favorite.create

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class FavoriteCreateDialogScreenViewModel(
    val createFavoriteUseCase: CreateFavoriteUseCase,
    val addFavoriteFileUseCase: AddFavoriteFileUseCase,
) : ViewModel()
