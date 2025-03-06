package com.sorrowblue.comicviewer.data.database

import androidx.room.RoomDatabase
import com.sorrowblue.comicviewer.data.database.dao.BookshelfDao
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger
import org.koin.test.KoinTest

internal const val TEST_DB_NAME = "migration-test"

internal expect inline fun <reified T : RoomDatabase> createDatabase(): T

internal expect fun setupComicViewerDatabaseTest()
internal expect fun releaseComicViewerDatabaseTest()

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class ComicViewerDatabaseTest : KoinTest {

    private lateinit var bookshelfDao: BookshelfDao
    private lateinit var db: ComicViewerTestDatabase

    @BeforeTest
    fun setupDatabase() {
        setupComicViewerDatabaseTest()
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
        db = createDatabase<ComicViewerTestDatabase>()
        bookshelfDao = db.bookshelfDao()
    }

    @AfterTest
    fun closeDatabase() {
        db.close()
        releaseComicViewerDatabaseTest()
    }

    @Test
    fun testInsert() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        val bookshelfEntity = bookshelfDao.flow(column.toInt()).first()
        assertEquals(bookshelfEntity, server.copy(BookshelfId(column.toInt())))
    }

    @Test
    fun testUpsert() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server).toInt()
        val update = server.copy(id = BookshelfId(column), displayName = "UpdateName")
        bookshelfDao.upsert(update)
        val bookshelfEntity = bookshelfDao.flow(column).first()
        assertEquals(bookshelfEntity, update)
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
