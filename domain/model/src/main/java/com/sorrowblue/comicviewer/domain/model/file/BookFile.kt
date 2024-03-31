package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.extension
import kotlinx.parcelize.Parcelize

@Parcelize
data class BookFile(
    override val bookshelfId: BookshelfId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val isHidden: Boolean,
    override val cacheKey: String = "",
    override val lastPageRead: Int = 0,
    override val totalPageCount: Int = 0,
    override val lastReadTime: Long = 0,
    override val params: Map<String, String?> = emptyMap(),
    override val sortIndex: Int = -1,
) : Book {

    val extension get() = path.extension
}
