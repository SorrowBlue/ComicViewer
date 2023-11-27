package com.sorrowblue.comicviewer.domain.service.di

import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageBookSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageFolderSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageViewerOperationSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageViewerSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Binds
    @Singleton
    fun bindManageDisplaySettingsUseCase(
        interactor: ManageDisplaySettingsInteractor,
    ): ManageDisplaySettingsUseCase

    @Binds
    fun bindManageFolderSettingsUseCase(
        interactor: ManageFolderSettingsInteractor,
    ): ManageFolderSettingsUseCase

    @Binds
    @Singleton
    fun bindManageViewerSettingsUseCase(
        interactor: ManageViewerSettingsInteractor,
    ): ManageViewerSettingsUseCase

    @Binds
    @Singleton
    fun bindManageBookSettingsUseCase(
        interactor: ManageBookSettingsInteractor,
    ): ManageBookSettingsUseCase

    @Binds
    @Singleton
    fun bindManageViewerOperationSettingsUseCase(
        interactor: ManageViewerOperationSettingsInteractor,
    ): ManageViewerOperationSettingsUseCase
}
