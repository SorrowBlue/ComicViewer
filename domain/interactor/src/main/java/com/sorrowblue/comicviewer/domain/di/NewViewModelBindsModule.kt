package com.sorrowblue.comicviewer.domain.di

import com.sorrowblue.comicviewer.domain.usecase.LoadServerPagingDataUseCase
import com.sorrowblue.comicviewer.domain.usecase.interactor.LoadServerPagingDataInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class NewViewModelBindsModule {

    @Binds
    abstract fun bindLoadServerPagingDataUseCase(interactor: LoadServerPagingDataInteractor): LoadServerPagingDataUseCase
}
