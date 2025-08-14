package com.sorrowblue.comicviewer.data.database

import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfEntity
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.DecryptedPassword
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class EntityFactory {

    fun createBookshelfEntity(id: Int = 0, deleted: Boolean = false): BookshelfEntity {
        return BookshelfEntity(
            id = BookshelfId(id),
            displayName = "displayName$id",
            type = BookshelfEntity.Type.INTERNAL,
            deleted = deleted,
            host = "",
            port = 0,
            domain = "",
            username = "",
            password = DecryptedPassword("")
        )
    }

    @OptIn(ExperimentalTime::class)
    fun createFileEntity(
        index: Int,
        bookshelfId: BookshelfId = BookshelfId(1),
        parent: String = "parent$index",
        lastReadTime: Long = Clock.System.now().toEpochMilliseconds(),
    ): FileEntity {
        return BookFile(
            bookshelfId = bookshelfId,
            name = "name$index",
            parent = parent,
            path = "/parent/path/name$index",
            size = 1024,
            lastModifier = Clock.System.now().toEpochMilliseconds(),
            isHidden = false,
            lastPageRead = 0,
            lastReadTime = lastReadTime
        ).let {
            FileEntity.Companion.fromModel(it)
        }
    }

    fun createCollectionEntity(): CollectionEntity {
        return CollectionEntity(
            id = CollectionId.Companion(),
            name = "name",
            type = CollectionEntity.Type.Basic,
            bookshelfId = null,
            searchCondition = null,
            createdAt = "",
            updatedAt = ""
        )
    }
}
