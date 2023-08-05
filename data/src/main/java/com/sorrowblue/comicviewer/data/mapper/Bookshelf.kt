package com.sorrowblue.comicviewer.data.mapper

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer

internal fun BookshelfModel.SmbServer.Auth.Companion.from(auth: SmbServer.Auth) =
    when (auth) {
        SmbServer.Auth.Guest -> BookshelfModel.SmbServer.Guest
        is SmbServer.Auth.UsernamePassword -> BookshelfModel.SmbServer.UsernamePassword(
            auth.domain,
            auth.username,
            auth.password
        )
    }

internal fun Bookshelf.toBookshelfModel() = when (this) {
    is InternalStorage -> BookshelfModel.InternalStorage(
        id = BookshelfModelId(id.value),
        name = displayName,
        fileCount = fileCount
    )

    is SmbServer -> BookshelfModel.SmbServer(
        id = BookshelfModelId(id.value),
        name = displayName,
        host = host,
        port = port,
        auth = BookshelfModel.SmbServer.Auth.from(auth),
        fileCount = fileCount
    )
}

internal fun BookshelfModel.Companion.from(bookshelf: Bookshelf) = when (bookshelf) {
    is InternalStorage -> BookshelfModel.InternalStorage(
        id = BookshelfModelId.from(bookshelf.id),
        name = bookshelf.displayName,
        fileCount = bookshelf.fileCount
    )

    is SmbServer -> BookshelfModel.SmbServer(
        id = BookshelfModelId.from(bookshelf.id),
        name = bookshelf.displayName,
        host = bookshelf.host,
        port = bookshelf.port,
        auth = BookshelfModel.SmbServer.Auth.from(bookshelf.auth),
        fileCount = bookshelf.fileCount
    )
}

fun BookshelfModel.toBookshelf() = when (this) {
    is BookshelfModel.InternalStorage ->
        InternalStorage(
            id = id.toBookshelfId(),
            displayName = name,
            fileCount = fileCount
        )

    is BookshelfModel.SmbServer -> {
        SmbServer(
            id = id.toBookshelfId(),
            displayName = name,
            host = host,
            port = port,
            auth = when (val auth = auth) {
                BookshelfModel.SmbServer.Guest -> SmbServer.Auth.Guest
                is BookshelfModel.SmbServer.UsernamePassword ->
                    SmbServer.Auth.UsernamePassword(auth.domain, auth.username, auth.password)
            },
            fileCount = fileCount
        )
    }
}
