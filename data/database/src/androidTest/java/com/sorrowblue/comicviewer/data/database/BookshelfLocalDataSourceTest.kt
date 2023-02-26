package com.sorrowblue.comicviewer.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.DecryptedPassword
import com.sorrowblue.comicviewer.data.database.entity.Bookshelf
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class BookshelfLocalDataSourceTest {
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
        val column = bookshelfDao.insert(server)
        Assert.assertEquals(bookshelfDao.selectById(column.toInt()).first(), server.copy(column.toInt()))
    }

    @Test
    fun upsertTest() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        logcat { "before=${server.id}, after=${column}" }
        Assert.assertEquals(bookshelfDao.selectById(column.toInt()).first(), server.copy(column.toInt()))
    }

    @Test
    fun testUpdate() = runTest {
        val server = randomServer()
        val column = bookshelfDao.insert(server)
        println("update id = ${column.toInt()}: ${bookshelfDao.update(server.copy(id = column.toInt()))}")
        println("update id = 5: ${bookshelfDao.update(server.copy(id = 5))}")
    }

    @Test
    fun testInsertDuplicate() = runTest {
        val server = randomServer()
        val column = bookshelfDao.insert(server)
        kotlin.runCatching {
            bookshelfDao.insert(randomServer(column.toInt()))
        }.let {
            Assert.assertThrows("", Exception::class.java) {
                it.getOrThrow()
            }
        }
    }

    @Test
    fun testSelectById() = runTest {
        val server = randomServer()
        val column = bookshelfDao.insert(server)
        Assert.assertEquals(bookshelfDao.selectById(column.toInt()).first(), server.copy(column.toInt()))
    }

    private fun randomServer(id: Int = 0) = Bookshelf(
        id,
        "TestDisplayName_$id",
        Bookshelf.Type.SMB,
        "192.168.0.$id",
        445,
        "domain_$id",
        "test_username_$id",
        DecryptedPassword("pass_$id")
    )
}
