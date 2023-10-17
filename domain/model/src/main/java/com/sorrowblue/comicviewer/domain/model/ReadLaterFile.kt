package com.sorrowblue.comicviewer.domain.model

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

data class ReadLaterFile(val bookshelfId: BookshelfId, val path: String)
