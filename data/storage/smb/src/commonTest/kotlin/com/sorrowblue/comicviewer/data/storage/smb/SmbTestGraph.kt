package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(DataScope::class)
expect interface SmbTestGraph {

    val fileClientFactory: Map<FileClientType, FileClient.Factory<*>>
}
