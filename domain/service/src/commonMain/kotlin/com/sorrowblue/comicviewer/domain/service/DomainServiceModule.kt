package com.sorrowblue.comicviewer.domain.service

import di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@ComponentScan("com.sorrowblue.comicviewer.domain.service")
class DomainServiceModule {

    @IoDispatcher
    @Singleton
    fun provideIoDispatcherForKmp(): CoroutineDispatcher = Dispatchers.IO
}
