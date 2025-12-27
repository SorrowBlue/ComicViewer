package com.sorrowblue.comicviewer.data.storage.smb

import com.sorrowblue.comicviewer.data.smb.BuildTestConfig
import com.sorrowblue.comicviewer.data.storage.client.FileClient
import com.sorrowblue.comicviewer.data.storage.client.FileClientException
import com.sorrowblue.comicviewer.data.storage.client.FileClientType
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import dev.zacsweers.metro.createGraph
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
class SmbFileClientCommonTest {

    private val server
        get() = SmbServer(
            displayName = "Test",
            host = BuildTestConfig.smbHost,
            port = BuildTestConfig.smbPort,
            auth = SmbServer.Auth.UsernamePassword(
                domain = BuildTestConfig.smbDomain,
                username = BuildTestConfig.smbUsername,
                password = BuildTestConfig.smbPassword
            )
        )

    private fun createClient(): FileClient<Bookshelf> {
        val graph = createGraph<SmbTestGraph>()
        @Suppress("UNCHECKED_CAST")
        return (graph.fileClientFactory.getValue(FileClientType.Smb) as FileClient.Factory<Bookshelf>)
            .create(server)
    }

    @Test
    fun testExists() = runTest {
        val client = createClient()
        assertTrue(
            BuildTestConfig.smbPath.isNotEmpty(),
            "BuildTestConfig.smbPath must be provided for tests"
        )
        assertTrue(client.exists(BuildTestConfig.smbPath))
    }

    @Test
    fun testConnect() = runTest {
        val client = createClient()
        client.connect(BuildTestConfig.smbPath)

        val bad = "/nope-nope-12345/"
        val ex = assertFailsWith<Throwable> { client.connect(bad) }
        assertTrue(ex is FileClientException || ex is Exception)
    }

    @Test
    fun testCurrentAndAttribute() = runTest {
        val client = createClient()
        val f = client.current(BuildTestConfig.smbPath)
        assertNotNull(f)
        assertTrue(f.path.contains(BuildTestConfig.smbPath.removeSuffix("/")))

        val attr = client.attribute(BuildTestConfig.smbPath)
        assertNotNull(attr)
        if (f !is Folder) {
            assertFalse(attr.directory)
        }
    }

    @Test
    fun testListFiles() = runTest {
        val client = createClient()
        val path = BuildTestConfig.smbPath
        val parent = path.trimEnd('/').substringBeforeLast('/', "")
        val dir = if (parent.isEmpty()) "/" else "$parent/"
        val file = Folder(
            path = dir,
            bookshelfId = server.id,
            name = dir.trimEnd('/'),
            parent = "",
            size = 0,
            lastModifier = 0,
            isHidden = false
        )
        val list = client.listFiles(file)
        assertTrue(list.isNotEmpty(), "listFiles should return entries for $dir")
        assertTrue(list.any {
            it.path == BuildTestConfig.smbPath || it.path.contains(
                BuildTestConfig.smbPath.trimEnd(
                    '/'
                )
            )
        })
    }
}
