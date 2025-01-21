package com.sorrowblue.comicviewer.data.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger
import org.junit.After
import org.junit.Before
import org.junit.Test

class BookshelfEntityLocalDataSourceTest {
    private lateinit var bookshelfDao: BookshelfDao
    private lateinit var db: ComicViewerDatabase

    @Before
    fun createDb() {
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
        val context = InstrumentationRegistry.getInstrumentation().context
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
        assertThat(bookshelfDao.flow(column.toInt()).first()).isEqualTo(
            server.copy(
                BookshelfId(
                    column.toInt()
                )
            )
        )
    }

    @Test
    fun testUpsert() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server).toInt()
        val update = server.copy(id = BookshelfId(column), displayName = "UpdateName")
        bookshelfDao.upsert(update)
        assertThat(bookshelfDao.flow(column).first()).isEqualTo(update)
    }

    private fun randomServer(id: Int = 0) = BookshelfEntity(
        BookshelfId(id),
        "TestDisplayName_$id",
        BookshelfEntity.Type.SMB,
        false,
        "192.168.0.$id",
        445,
        "domain_$id",
        "test_username_$id",
        DecryptedPassword("pass_$id")
    )
}
