package com.sorrowblue.comicviewer.feature.collection.editor.smart.section

import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.serialization.Serializable

@Serializable
internal data class SmartCollectionForm(
    val name: String = "",
    val bookshelfId: BookshelfId? = null,
    val searchCondition: SearchCondition = SearchCondition(),
)
