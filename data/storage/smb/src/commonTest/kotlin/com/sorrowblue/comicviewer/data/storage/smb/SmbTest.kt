package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class SmbTest {
    @Test
    fun testSmbServerConnection() {
        val graph = createGraph<TestGraph>()
        runTest {
            val server = SmbServer(
                "Test",
                "10.0.2.2",
                445,
                SmbServer.Auth.UsernamePassword("", "testuser", "testpass"),
            )
            val factory = graph.fileClientFactory.getValue(
                FileClientType.Smb,
            ) as FileClient.Factory<Bookshelf>
            val smbServer = factory.create(server)
            assertTrue(smbServer.exists("/testshare/"))
        }
    }
}

@DependencyGraph(DataScope::class)
internal interface TestGraph {
    val fileClientFactory: Map<FileClientType, FileClient.Factory<*>>
}
