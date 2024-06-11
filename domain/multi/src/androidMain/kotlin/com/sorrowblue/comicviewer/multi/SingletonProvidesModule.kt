package com.sorrowblue.comicviewer.multi

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//internal object SingletonProvidesModule {
//
//    @Singleton
//    @Provides
//    @Inject
//    fun bindSupportedImage(): MultiUseCase = MultiUseCaseImpl()
//}

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @Singleton
    @Binds
    fun bindSupportedImage(usecase: MultiUseCaseImpl): MultiUseCase
}

actual typealias Inject = javax.inject.Inject
