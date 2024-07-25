package com.sorrowblue.comicviewer.framework.preview

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.Folder

val LoremIpsum = listOf(
    "Lorem ipsum dolor",
    "Vivamus vitae sapien sodales",
    "Pellentesque non justo faucibus",
    "Donec ut eros",
    "Donec venenatis nisl eget sapien lacinia",
    "Fusce laoreet sapien vel nisi porta",
    "Nullam vestibulum",
    "Curabitur mollis eros",
    "Nam quis eros quis risus",
    "Nulla vehicula leo vel faucibus egestas",
    "Vivamus vel mauris dignissim",
    "Maecenas semper risus at imperdiet auctor",
    "Praesent",
    "Pellentesque dignissim",
    "Suspendisse",
    "Nam sit amet",
    "Suspendisse",
    "Vivamus pretium dui",
    "Suspendisse eu ante",
    "Nam ac nisl ac tellus pellentesque"
)

fun nextLoremIpsum() = LoremIpsum[LoremIpsumIndex++].also {
    if (LoremIpsumIndex >= LoremIpsum.size) LoremIpsumIndex = 0
}

private var LoremIpsumIndex = 0

private var size = 100L

fun fakeBookFile(bookshelfId: BookshelfId = BookshelfId(0), name: String = nextLoremIpsum()) =
    BookFile(
        bookshelfId,
        name,
        "parent",
        "path${bookshelfId.value}",
        (size * 2).also { size = it },
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
        nextLoremIpsum(),
        "name",
        "name",
        0L,
        0,
        false,
        emptyMap(),
        0,
        0,
    )
