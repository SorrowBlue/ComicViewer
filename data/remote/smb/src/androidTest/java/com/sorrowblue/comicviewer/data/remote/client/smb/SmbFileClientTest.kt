package com.sorrowblue.comicviewer.data.remote.client.smb

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.remote.client.FileClientException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SmbFileClientTest {

    @Before
    fun before() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
    }

    private val bookshelfModel = BookshelfModel.SmbServer(
        id = BookshelfModelId(0),
        name = "",
        host = "192.168.0.101",
        port = 445,
        auth = BookshelfModel.SmbServer.UsernamePassword(
            "",
            "SorrowBlue",
            "nasyuukiasuna2s2"
        ),
        0
    )

    private val path = "/test/[0]TEST/"

    @Test
    fun authErrorTest() {
        val client = SmbFileClient(bookshelfModel.copy(auth = BookshelfModel.SmbServer.UsernamePassword("", "", "")))
        Assert.assertThrows(FileClientException.InvalidAuth::class.java) {
            runTest {
                val isExists = client.exists(path)
                println("isExists=$isExists")
            }
        }
    }

    @Test
    fun badHostTest() {
        val client = SmbFileClient(bookshelfModel.copy(host = "192.168.0.100"))
        runTest {
            val isExists = client.exists(path)
            Assert.assertFalse(isExists)
        }
    }

    @Test
    fun badPortTest() {
        val client = SmbFileClient(bookshelfModel.copy(port = 999))
        runTest {
            val isExists = client.exists(path)
            Assert.assertFalse(isExists)
        }
    }

    @Test
    fun badShareNameTest() {
        val client = SmbFileClient(bookshelfModel)
        runTest {
            val isExists = client.exists("/dummy/[0]TEST_error/")
            Assert.assertFalse(isExists)
        }
    }

    @Test
    fun noPathTest() {
        val client = SmbFileClient(bookshelfModel)
        runTest {
            val isExists = client.exists("/test/[0]TEST_error/")
            Assert.assertFalse(isExists)
        }
    }

    @Test
    fun successTest() {
        val client = SmbFileClient(bookshelfModel)
        runTest {
            val isExists = client.exists("${path}test.pdf")
            Assert.assertTrue(isExists)
        }
    }

    @Test
    fun currentTest() {
        val client = SmbFileClient(bookshelfModel)
        runTest {
            val fileModel = client.current("${path}test.pdf")
            Assert.assertEquals(fileModel.name, "test.pdf")
        }
    }

    @Test
    fun currentTest_noPath() {
        val client = SmbFileClient(bookshelfModel)
        Assert.assertThrows(FileClientException.InvalidPath::class.java) {
            runTest {
                client.current("/test/test.pdfa")
            }
        }
    }

    @Test
    fun inputStreamTest() {
        val client = SmbFileClient(bookshelfModel)
        runTest {
            val fileModel = client.current("${path}test.txt")
            client.inputStream(fileModel).use {
                println(it.readBytes().decodeToString())
                Assert.assertNotNull(it)
            }
        }
    }
}
