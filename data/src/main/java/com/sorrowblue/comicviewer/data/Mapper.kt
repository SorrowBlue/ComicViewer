package com.sorrowblue.comicviewer.data

import com.sorrowblue.comicviewer.data.common.BookshelfFolderModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteFileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.BookFolder
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder

internal fun BookshelfModelId.toServerId() = BookshelfId(value)

internal fun BookshelfFolderModel.toServerFolder() =
    BookshelfFolder(value.first.toServer() to value.second.toFolder())

internal fun FileModel.Folder.toFolder() = Folder(
    bookshelfId = bookshelfModelId.toServerId(),
    name = name,
    parent = parent,
    path = path,
    size = size,
    lastModifier = lastModifier
)

internal fun SmbServer.Auth.toServerModelAuth(): BookshelfModel.SmbServer.Auth {
    return when (this) {
        SmbServer.Auth.Guest -> BookshelfModel.SmbServer.Guest
        is SmbServer.Auth.UsernamePassword -> BookshelfModel.SmbServer.UsernamePassword(
            domain,
            username,
            password
        )
    }
}

internal fun Bookshelf.toServerModel() = when (this) {
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
        auth = auth.toServerModelAuth(),
        fileCount = fileCount
    )
}

internal fun BookshelfModel.toServer() = when (this) {
    is BookshelfModel.InternalStorage ->
        InternalStorage(
            id = id.toServerId(),
            displayName = name,
            fileCount = fileCount
        )

    is BookshelfModel.SmbServer -> {
        SmbServer(
            id = id.toServerId(),
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

internal fun FavoriteModel.toFavorite(): Favorite {
    return Favorite(FavoriteId(id.value), name, count)
}

internal fun FavoriteFile.toFavoriteBookModel(): FavoriteFileModel {
    return FavoriteFileModel(FavoriteModelId(id.value), BookshelfModelId(bookshelfId.value), path)
}

internal fun FileModel.toFile(): File {
    return when (this) {
        is FileModel.File -> BookFile(
            bookshelfModelId.toServerId(),
            name,
            parent,
            path,
            size,
            lastModifier,
            cacheKey,
            lastReadPage,
            totalPageCount,
            lastRead
        )

        is FileModel.Folder -> Folder(
            bookshelfModelId.toServerId(),
            name,
            parent,
            path,
            size,
            lastModifier
        )

        is FileModel.ImageFolder -> BookFolder(
            bookshelfModelId.toServerId(),
            name,
            parent,
            path,
            size,
            lastModifier,
            cacheKey,
            lastReadPage,
            totalPageCount,
            lastRead
        )
    }
}

internal fun File.toFileModel(): FileModel {
    return when (this) {
        is Folder -> FileModel.Folder(
            path = path,
            bookshelfModelId = BookshelfModelId(bookshelfId.value),
            name = name,
            parent = parent,
            size = size,
            lastModifier = lastModifier,
            sortIndex = 0
        )

        is BookFile -> FileModel.File(
            path = path,
            bookshelfModelId = BookshelfModelId(bookshelfId.value),
            name = name,
            parent = parent,
            size = size,
            lastModifier = lastModifier,
            sortIndex = 0,
            cacheKey = cacheKey,
            totalPageCount = totalPageCount,
            lastReadPage = lastPageRead,
            lastRead = lastReadTime
        )

        is BookFolder -> FileModel.ImageFolder(
            path = path,
            bookshelfModelId = BookshelfModelId(bookshelfId.value),
            name = name,
            parent = parent,
            size = size,
            lastModifier = lastModifier,
            sortIndex = 0,
            cacheKey = cacheKey,
            totalPageCount = totalPageCount,
            lastReadPage = lastPageRead,
            lastRead = lastReadTime
        )
    }
}
