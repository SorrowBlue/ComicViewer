package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.qualifier.SmbFileClientFactory
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
