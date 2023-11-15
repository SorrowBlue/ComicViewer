package com.sorrowblue.comicviewer.data.service.di

import com.sorrowblue.comicviewer.data.infrastructure.repository.impl.FileScanService
import com.sorrowblue.comicviewer.data.service.FileScanServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Singleton
    @Binds
    fun bindExampleService(repository: FileScanServiceImpl): FileScanService
}
