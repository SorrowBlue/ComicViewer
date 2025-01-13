package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import kotlin.jvm.JvmInline

@JvmInline
value class NavigationHistory(val value: Pair<List<Folder>, Book>) {
    val folderList get() = value.first

    constructor(folderList: List<Folder>, book: Book) : this(
        Pair(
            folderList,
            book
        )
    )
}
