package com.sorrowblue.comicviewer.data.database.dao

import com.sorrowblue.comicviewer.data.database.DatabaseTest
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.framework.test.MultiplatformAndroidJUnit4
import com.sorrowblue.comicviewer.framework.test.MultiplatformRunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

@MultiplatformRunWith(MultiplatformAndroidJUnit4::class)
internal class CollectionDaoTest : DatabaseTest() {
    private val collectionDao: CollectionDao get() = db.collectionDao()

    @Test
    fun insert_flow() = runTest {
        val entity = factory.createCollectionEntity()
        val inserted = collectionDao.insert(entity)
        val dbEntity = collectionDao.flow(CollectionId(inserted.toInt())).first()?.entity
        assertEquals(CollectionId(1), dbEntity?.id)
        assertEquals(entity.name, dbEntity?.name)
        assertEquals(entity.type, dbEntity?.type)
        assertEquals(entity.bookshelfId, dbEntity?.bookshelfId)
        assertEquals(entity.searchCondition, dbEntity?.searchCondition)
        assertNotEquals(entity.createdAt, dbEntity?.createdAt)
        assertNotEquals(entity.updatedAt, dbEntity?.updatedAt)
    }

    @Test
    fun flowAll() = runTest {
        val entityList = List(100) {
            factory.createCollectionEntity()
        }
        entityList.forEach {
            collectionDao.insert(it)
        }
        val dbEntityList = collectionDao.flowAll().first()
        assertEquals(entityList.size, dbEntityList.size)
    }

    @Test
    fun updateBasic() = runTest {
        var entity = CollectionEntity.fromModel(BasicCollection("name"))
        val inserted = collectionDao.insert(entity)
        var dbEntity = collectionDao.flow(CollectionId(inserted.toInt())).first()?.entity
        entity = dbEntity!!.copy(name = "updated")
        collectionDao.update(entity)
        dbEntity = collectionDao.flow(entity.id).first()?.entity
        assertEquals(entity.id, dbEntity?.id)
        assertEquals(entity.name, dbEntity?.name)
        assertEquals(entity.type, dbEntity?.type)
        assertEquals(entity.bookshelfId, dbEntity?.bookshelfId)
        assertEquals(entity.searchCondition, dbEntity?.searchCondition)
        assertEquals(entity.createdAt, dbEntity?.createdAt)
    }
    /*
        @Test
        fun updateSmart() = runTest {
            var entity = CollectionEntity.fromModel(SmartCollection("name", null, SearchCondition()))
            val inserted = collectionDao.insert(entity)
            var collection = collectionDao.flow(CollectionId(inserted.toInt())).first()?.toModel()
            assertIs<SmartCollection>(collection)
            collection.copy(
                name = "",
                bookshelfId = null,
                searchCondition = SearchCondition(),
            )
            entity = collection!!.copy(name = "updated")
            collectionDao.update(entity)
            collection = collectionDao.flow(entity.id).first()?.entity
            assertEquals(entity, collection)
        } */
}
