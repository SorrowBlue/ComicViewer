package com.sorrowblue.comicviewer.data.service.di

import com.sorrowblue.comicviewer.data.reporitory.FileScanService
import com.sorrowblue.comicviewer.data.service.FileScanServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @Singleton
    @Binds
    abstract fun bindExampleService(repository: FileScanServiceImpl): FileScanService
}
