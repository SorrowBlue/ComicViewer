package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId

data class ReadLaterFile(val bookshelfId: BookshelfId, val path: String)
