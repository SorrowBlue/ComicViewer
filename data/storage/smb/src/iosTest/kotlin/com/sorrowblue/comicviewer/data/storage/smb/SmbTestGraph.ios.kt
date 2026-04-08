package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.data.storage.smb.di.StorageSmbProviders
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@DependencyGraph(scope = DataScope::class)
actual interface SmbTestGraph : StorageSmbProviders {
    @Multibinds(allowEmpty = true)
    actual val fileClientFactory: FileClientFactory

    @IoDispatcher
    @Provides
    private fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
