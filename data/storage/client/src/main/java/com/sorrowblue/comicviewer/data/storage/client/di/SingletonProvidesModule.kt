package com.sorrowblue.comicviewer.data.storage.client.di

import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @ImageExtension
    @Singleton
    @Provides
    fun bindSupportedImage(): Set<String> = SUPPORTED_IMAGE
}
