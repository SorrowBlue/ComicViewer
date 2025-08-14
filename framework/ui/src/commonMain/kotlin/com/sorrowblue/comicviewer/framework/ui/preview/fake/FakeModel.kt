package com.sorrowblue.comicviewer.framework.ui.preview.fake

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.Folder

@OptIn(InternalDataApi::class)
fun fakeInternalStorage(bookshelfId: Int = 0, name: String = nextLoremIpsum()) =
    InternalStorage(
        id = BookshelfId(bookshelfId),
        displayName = name,
        fileCount = 999,
        isDeleted = false
    )

@OptIn(InternalDataApi::class)
fun fakeSmbServer(bookshelfId: Int = 0, name: String = nextLoremIpsum()) =
    SmbServer(
        id = BookshelfId(bookshelfId),
        displayName = name,
        host = "198.51.100.254",
        port = 455,
        auth = SmbServer.Auth.UsernamePassword("example.com", nextLoremIpsum(), nextLoremIpsum())
    )

@OptIn(InternalDataApi::class)
fun fakeBookFile(bookshelfId: Int = 0, name: String = nextLoremIpsum()) =
    BookFile(
        bookshelfId = BookshelfId(bookshelfId),
        name = "$name.zip",
        parent = "parent",
        path = "path$bookshelfId",
        size = 10240000,
        lastModifier = 100,
        isHidden = false,
        totalPageCount = 100,
        lastPageRead = 50
    )

@OptIn(InternalDataApi::class)
fun fakeFolder(bookshelfId: Int = 0) =
    Folder(
        bookshelfId = BookshelfId(bookshelfId),
        name = nextLoremIpsum(),
        parent = "name",
        path = "name",
        size = 0L,
        lastModifier = 0,
        isHidden = false,
    )
