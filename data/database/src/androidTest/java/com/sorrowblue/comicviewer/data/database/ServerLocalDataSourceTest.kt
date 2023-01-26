package com.sorrowblue.comicviewer.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sorrowblue.comicviewer.data.database.dao.ServerDao
import com.sorrowblue.comicviewer.data.database.entity.DecryptedPassword
import com.sorrowblue.comicviewer.data.database.entity.Server
import java.util.logging.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ServerLocalDataSourceTest {
    private lateinit var serverDao: ServerDao
    private lateinit var db: ComicViewerDatabase

    @Before
    fun createDb() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ComicViewerDatabase::class.java).build()
        serverDao = db.serverDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsert() = runTest {
        val server = randomServer()
        val column = serverDao.insert(server)
        Assert.assertEquals(serverDao.selectById(column.toInt()), server.copy(column.toInt()))
    }

    @Test
    fun upsertTest() = runTest {
        val server = randomServer()
        val column = serverDao.upsert(server)
        logcat { "before=${server.id}, after=${column}" }
        Assert.assertEquals(serverDao.selectById(column.toInt()), server.copy(column.toInt()))
    }

    @Test
    fun testUpdate() = runTest {
        val server = randomServer()
        val column = serverDao.insert(server)
        println("update id = ${column.toInt()}: ${serverDao.update(server.copy(id = column.toInt()))}")
        println("update id = 5: ${serverDao.update(server.copy(id = 5))}")
    }

    @Test
    fun testInsertDuplicate() = runTest {
        val server = randomServer()
        val column = serverDao.insert(server)
        kotlin.runCatching {
            serverDao.insert(randomServer(column.toInt()))
        }.let {
            Assert.assertThrows("", Exception::class.java) {
                it.getOrThrow()
            }
        }
    }

    @Test
    fun testSelectById() = runTest {
        val server = randomServer()
        val column = serverDao.insert(server)
        Assert.assertEquals(serverDao.selectById(column.toInt()), server.copy(column.toInt()))
    }

    private fun randomServer(id: Int = 0) = Server(
        id,
        "TestDisplayName_$id",
        Server.Type.SMB,
        "192.168.0.$id",
        445,
        "domain_$id",
        "test_username_$id",
        DecryptedPassword("pass_$id")
    )
}
