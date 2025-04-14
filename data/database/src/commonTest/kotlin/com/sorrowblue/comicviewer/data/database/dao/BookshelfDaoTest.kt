package com.sorrowblue.comicviewer.data.database.dao

import com.sorrowblue.comicviewer.data.database.TestMan
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.test.inject

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class BookshelfDaoTest : TestMan() {

    private val bookshelfDao: BookshelfDao by inject()

    @Test
    fun testInsert() = runTest {
        val server = randomServer()
        val column = bookshelfDao.upsert(server)
        val bookshelfEntity = bookshelfDao.flow(column.toInt()).first()
        assertEquals(bookshelfEntity, server.copy(BookshelfId(column.toInt())))
        assertEquals(bookshelfEntity?.password, server.password)
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
