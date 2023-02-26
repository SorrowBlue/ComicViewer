package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.Folder

@JvmInline
value class NavigationHistory(val triple: Triple<Bookshelf, List<Folder>, Int>) {
    constructor(bookshelf: Bookshelf, folderList: List<Folder>, position: Int) : this(
        Triple(
            bookshelf,
            folderList,
            position
        )
    )
}
