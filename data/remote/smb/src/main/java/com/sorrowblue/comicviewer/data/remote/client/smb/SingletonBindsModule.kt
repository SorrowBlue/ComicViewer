package com.sorrowblue.comicviewer.data.remote.client.smb

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.remote.client.FileClient
import com.sorrowblue.comicviewer.data.remote.client.qualifier.SmbFileClientFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SingletonBindsModule {

    @SmbFileClientFactory
    @Binds
    abstract fun bindSmbFileClientFactory(factory: SmbFileClient.Factory): FileClient.Factory<BookshelfModel.SmbServer>
}
