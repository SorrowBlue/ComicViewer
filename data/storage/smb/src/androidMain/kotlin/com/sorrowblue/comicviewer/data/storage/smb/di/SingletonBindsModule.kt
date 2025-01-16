package com.sorrowblue.comicviewer.data.storage.smb.di

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClientFactory
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface SingletonBindsModule {

    @SmbFileClientFactory
    @Binds
    fun bindSmbFileClientFactory(factory: com.sorrowblue.comicviewer.data.storage.smb.impl.SmbFileClientFactory): FileClient.Factory<SmbServer>
}
