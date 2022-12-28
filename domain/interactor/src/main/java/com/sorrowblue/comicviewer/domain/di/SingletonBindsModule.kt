package com.sorrowblue.comicviewer.domain.di

import com.sorrowblue.comicviewer.domain.usecase.interactor.DisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.ViewerOperationSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.ViewerSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.settings.DisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ViewerSettingsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Binds
    @Singleton
    abstract fun bindDisplaySettingsUseCase(interactor: DisplaySettingsInteractor): DisplaySettingsUseCase

    @Binds
    @Singleton
    abstract fun bindViewerSettingsUseCase(interactor: ViewerSettingsInteractor): ViewerSettingsUseCase
    @Binds
    @Singleton
    abstract fun bindViewerOperationSettingsUseCase(interactor: ViewerOperationSettingsInteractor): ViewerOperationSettingsUseCase
}
