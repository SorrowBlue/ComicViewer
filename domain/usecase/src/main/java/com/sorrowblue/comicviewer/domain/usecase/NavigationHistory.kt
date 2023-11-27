package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder

@JvmInline
value class NavigationHistory(val triple: Triple<Bookshelf, List<Folder>, Book>) {
    constructor(bookshelf: Bookshelf, folderList: List<Folder>, book: Book) : this(
        Triple(
            bookshelf,
            folderList,
            book
        )
    )
}
