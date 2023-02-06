package com.sorrowblue.comicviewer.domain.entity

import com.sorrowblue.comicviewer.domain.entity.server.BookshelfId

data class ReadLaterFile(val bookshelfId: BookshelfId, val path: String)
