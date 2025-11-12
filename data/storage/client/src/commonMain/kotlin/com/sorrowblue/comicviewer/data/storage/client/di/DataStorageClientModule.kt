package com.sorrowblue.comicviewer.data.storage.client.di

import com.sorrowblue.comicviewer.data.storage.client.impl.RemoteDataSourceImpl
import com.sorrowblue.comicviewer.data.storage.client.qualifier.ImageExtension
import com.sorrowblue.comicviewer.domain.model.SUPPORTED_IMAGE
import com.sorrowblue.comicviewer.domain.service.datasource.RemoteDataSource
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(DataScope::class)
interface DataStorageClientModule {
    @Provides
    @ImageExtension
    fun bindSupportedImage(): Set<String> = SUPPORTED_IMAGE

    @Binds
    private fun RemoteDataSourceImpl.Factory.bind(): RemoteDataSource.Factory = this
}
