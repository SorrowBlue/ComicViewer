package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import com.sorrowblue.comicviewer.framework.common.IoDispatcher
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@DependencyGraph(scope = AppScope::class)
actual interface SmbTestGraph {
    @Multibinds(allowEmpty = true)
    actual val fileClientFactory: FileClientFactory

    @IoDispatcher
    @Provides
    private fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
