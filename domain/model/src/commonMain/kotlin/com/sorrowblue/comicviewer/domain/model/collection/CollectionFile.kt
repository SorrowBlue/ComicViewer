package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class CollectionFile(val id: CollectionId, val bookshelfId: BookshelfId, val path: String)
