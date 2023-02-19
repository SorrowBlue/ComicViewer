package com.sorrowblue.comicviewer.domain.entity.file

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

data class BookFile(
    override val bookshelfId: BookshelfId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val cacheKey: String,
    override val lastPageRead: Int,
    override val totalPageCount: Int,
    override val lastReadTime: Long,
    override val params: Map<String, String?> = emptyMap()
) : Book
