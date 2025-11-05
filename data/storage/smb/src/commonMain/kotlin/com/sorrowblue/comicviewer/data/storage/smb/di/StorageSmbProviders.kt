package com.sorrowblue.comicviewer.data.storage.smb.di

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientKey
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.data.storage.smb.impl.SmbFileClient
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap

@ContributesTo(DataScope::class)
interface StorageSmbProviders {

    @Binds
    @IntoMap
    @FileClientKey(FileClientType.Smb)
    private fun SmbFileClient.Factory.bind(): FileClient.Factory<*> = this
}
