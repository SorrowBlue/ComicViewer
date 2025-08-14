package com.sorrowblue.comicviewer.framework.ui.preview.fake

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import kotlinx.datetime.LocalDateTime

@OptIn(InternalDataApi::class)
fun fakeSmartCollection(collectionId: Int = 0, name: String = nextLoremIpsum()) = SmartCollection(
    id = CollectionId(collectionId),
    name = name,
    count = 999,
    createdAt = fakeCreatedAt,
    updatedAt = fakeUpdatedAt,
    bookshelfId = null,
    searchCondition = SearchCondition(),
)

@OptIn(InternalDataApi::class)
fun fakeBasicCollection(collectionId: Int = 0, name: String = nextLoremIpsum()) =
    BasicCollection(
        id = CollectionId(collectionId),
        name = name,
        count = 999,
        createdAt = fakeCreatedAt,
        updatedAt = fakeUpdatedAt,
    )

val fakeCreatedAt get() = LocalDateTime(1970, 1, 1, 0, 0)
val fakeUpdatedAt get() = LocalDateTime(2261, 12, 31, 23, 59)
