package com.sorrowblue.comicviewer.data.storage.client.di

import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface DataStorageClientProviders {
    @Provides
    @ImageExtension
    fun bindSupportedImage(): Set<String> = SUPPORTED_IMAGE
}
