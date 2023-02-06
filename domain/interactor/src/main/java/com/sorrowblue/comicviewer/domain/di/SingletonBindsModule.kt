package com.sorrowblue.comicviewer.domain.di

import com.sorrowblue.comicviewer.domain.interactor.settings.ManageDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.interactor.settings.ManageViewerOperationSettingsInteractor
import com.sorrowblue.comicviewer.domain.interactor.settings.ManageViewerSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
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
    abstract fun bindManageDisplaySettingsUseCase(interactor: ManageDisplaySettingsInteractor): ManageDisplaySettingsUseCase

    @Binds
    @Singleton
    abstract fun bindManageViewerSettingsUseCase(interactor: ManageViewerSettingsInteractor): ManageViewerSettingsUseCase

    @Binds
    @Singleton
    abstract fun bindManageViewerOperationSettingsUseCase(interactor: ManageViewerOperationSettingsInteractor): ManageViewerOperationSettingsUseCase
}
