package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

interface FileScanService {

    suspend fun enqueue(
        bookshelfId: BookshelfId,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>,
    ): String
}
