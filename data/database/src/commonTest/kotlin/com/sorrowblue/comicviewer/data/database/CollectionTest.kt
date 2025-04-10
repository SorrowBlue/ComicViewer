package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.dao.CollectionDao
import com.sorrowblue.comicviewer.data.database.dao.insert
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import logcat.LogcatLogger
import logcat.PrintLogger
import logcat.logcat
import org.koin.test.KoinTest

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class CollectionTest : KoinTest {

    private lateinit var collectionDao: CollectionDao
    private lateinit var db: ComicViewerTestDatabase

    @BeforeTest
    fun setupDatabase() {
        setupComicViewerDatabaseTest()
        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(PrintLogger)
        }
        db = createDatabase<ComicViewerTestDatabase>()
        collectionDao = db.collectionDao()
    }

    @AfterTest
    fun closeDatabase() {
        db.close()
        releaseComicViewerDatabaseTest()
    }

    @Test
    fun testFlow() = runTest {
        collectionDao.insert(
            CollectionEntity(
                id = CollectionId(),
                name = "name",
                type = CollectionEntity.Type.Basic,
                bookshelfId = null,
                searchCondition = null,
                createdAt = "",
                updatedAt = ""
            )
        )
        collectionDao.insert(
            CollectionEntity(
                id = CollectionId(),
                name = "name",
                type = CollectionEntity.Type.Basic,
                bookshelfId = null,
                searchCondition = null,
                createdAt = "",
                updatedAt = ""
            )
        )
        collectionDao.flow2(CollectionId(1)).onEach {
            logcat { "$it" }
        }.first()
        collectionDao.flowAll().onEach {
            logcat { "$it" }
        }.first()
    }
}
