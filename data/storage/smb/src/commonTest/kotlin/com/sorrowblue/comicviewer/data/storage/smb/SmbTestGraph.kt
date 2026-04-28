package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClientFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds

@DependencyGraph(AppScope::class)
expect interface SmbTestGraph {
    @Multibinds(allowEmpty = true)
    val fileClientFactory: FileClientFactory
}
