package com.sorrowblue.comicviewer.data.database.dao

import com.sorrowblue.comicviewer.data.database.TestMan
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.Test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import logcat.logcat
import org.koin.test.inject

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class CollectionDaoTest : TestMan() {

    private val collectionDao: CollectionDao by inject()

    @Test
    fun testFlow() = runTest {
        collectionDao.insert(
            CollectionEntity(
                id = CollectionId.Companion(),
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
                id = CollectionId.Companion(),
                name = "name",
                type = CollectionEntity.Type.Basic,
                bookshelfId = null,
                searchCondition = null,
                createdAt = "",
                updatedAt = ""
            )
        )
        collectionDao.flow(CollectionId(1)).onEach {
            logcat { "$it" }
        }.first()
        collectionDao.flowAll().onEach {
            logcat { "$it" }
        }.first()
    }
}
