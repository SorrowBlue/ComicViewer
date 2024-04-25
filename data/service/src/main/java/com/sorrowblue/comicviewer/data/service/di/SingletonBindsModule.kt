package com.sorrowblue.comicviewer.data.service.di

import com.sorrowblue.comicviewer.data.service.FileScanServiceImpl
import com.sorrowblue.comicviewer.domain.service.FileScanService
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
