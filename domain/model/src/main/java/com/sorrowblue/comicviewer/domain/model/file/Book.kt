package com.sorrowblue.comicviewer.domain.model.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

sealed interface Book : File {
    val cacheKey: String
    val lastPageRead: Int
    val totalPageCount: Int
    val lastReadTime: Long
}

fun fakeBookFile(bookshelfId: BookshelfId = BookshelfId(0)) =
    BookFile(
        bookshelfId,
        "ブラックジャックによろしく 第01巻",
        "parent",
        "path${bookshelfId.value}",
        100,
        100,
        false,
        "",
        0,
        0,
        0,
        mapOf(),
        0,
    )

fun fakeFolder(bookshelfId: BookshelfId = BookshelfId(0)) =
    Folder(
        bookshelfId,
        "path",
        "name",
        "name",
        0L,
        0,
        false,
        emptyMap(),
        0,
        0,
    )
