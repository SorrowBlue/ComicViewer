package com.sorrowblue.comicviewer.data.storage.client.di

import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension2
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
// @ComponentScan("com.sorrowblue.comicviewer.data.storage.client")
class DataStorageClientModule

    @ImageExtension2
    @Singleton
    fun bindSupportedImage(): Set<String> = SUPPORTED_IMAGE
