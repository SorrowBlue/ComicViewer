package com.sorrowblue.comicviewer.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.DecryptedPasswordEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookshelfEntityLocalDataSourceTest {
    private lateinit var bookshelfDao: BookshelfDao
    private lateinit var db: ComicViewerDatabase

    @Before
    fun createDb() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, ComicViewerDatabase::class.java).build()
        bookshelfDao = db.bookshelfDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsert() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        Assert.assertEquals(
            bookshelfDao.flow(column.toInt()).first(),
            server.copy(column.toInt())
        )
    }

    @Test
    fun upsertTest() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        logcat { "before=${server.id}, after=${column}" }
        Assert.assertEquals(
            bookshelfDao.flow(column.toInt()).first(),
            server.copy(column.toInt())
        )
    }

    @Test
    fun testUpdate() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        println("update id = ${column.toInt()}: ${bookshelfDao.upsert(server.copy(id = column.toInt()))}")
        println("update id = 5: ${bookshelfDao.upsert(server.copy(id = 5))}")
    }

    @Test
    fun testInsertDuplicate() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        kotlin.runCatching {
            bookshelfDao.upsert(randomServer(column.toInt()))
        }.let {
            Assert.assertThrows("", Exception::class.java) {
                it.getOrThrow()
            }
        }
    }

    @Test
    fun testSelectById() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        Assert.assertEquals(
            bookshelfDao.flow(column.toInt()).first(),
            server.copy(column.toInt())
        )
    }

    private fun randomServer(id: Int = 0) = BookshelfEntity(
        id,
        "TestDisplayName_$id",
        BookshelfEntity.Type.SMB,
        "192.168.0.$id",
        445,
        "domain_$id",
        "test_username_$id",
        DecryptedPasswordEntity("pass_$id")
    )
}
