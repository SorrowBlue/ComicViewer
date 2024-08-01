package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Folder(
    override val bookshelfId: BookshelfId,
    override val name: String,
    override val parent: String,
    override val path: String,
    override val size: Long,
    override val lastModifier: Long,
    override val isHidden: Boolean,
    override val count: Int = 0,
    override val sortIndex: Int = -1,
    override val cacheKey: String = "",
) : IFolder
