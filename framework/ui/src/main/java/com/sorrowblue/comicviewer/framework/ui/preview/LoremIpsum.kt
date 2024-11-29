package com.sorrowblue.comicviewer.framework.ui.preview

import com.sorrowblue.comicviewer.domain.model.ExperimentalIdValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.Folder
import kotlin.random.Random

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

@OptIn(ExperimentalIdValue::class)
fun fakeInternalStorage(bookshelfId: Int = 0, name: String = nextLoremIpsum()) =
    InternalStorage(BookshelfId(bookshelfId), name, 999)

@OptIn(ExperimentalIdValue::class)
fun fakeSmbServer(bookshelfId: Int = 0, name: String = nextLoremIpsum()) =
    SmbServer(
        BookshelfId(bookshelfId),
        name,
        "198.51.100.254",
        455,
        SmbServer.Auth.UsernamePassword("example.com", nextLoremIpsum(), nextLoremIpsum())
    )

@OptIn(ExperimentalIdValue::class)
fun fakeBookFile(bookshelfId: Int = 0, name: String = nextLoremIpsum()) =
    BookFile(
        BookshelfId(bookshelfId),
        name,
        "parent",
        "path$bookshelfId",
        (size * 2).also { size = it },
        100,
        false,
    )

fun fakeFolder(bookshelfId: BookshelfId = BookshelfId()) =
    Folder(
        bookshelfId,
        nextLoremIpsum(),
        "name",
        "name",
        0L,
        0,
        false,
    )

fun fakeFavorite(favoriteId: Int = 0, exist: Boolean = Random(1).nextBoolean()) =
    Favorite(
        @OptIn(ExperimentalIdValue::class) FavoriteId(favoriteId),
        nextLoremIpsum(),
        Random(1).nextInt(5, 999),
        exist,
        Random(1).nextLong()
    )
