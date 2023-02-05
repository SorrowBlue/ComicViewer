package com.sorrowblue.comicviewer.data

import com.sorrowblue.comicviewer.data.common.DeviceStorageModel
import com.sorrowblue.comicviewer.data.common.FavoriteBookModel
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerFileModelFolder
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SmbServerModel
import com.sorrowblue.comicviewer.domain.entity.ServerFolder
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteBook
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.BookFolder
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.server.DeviceStorage
import com.sorrowblue.comicviewer.domain.entity.server.Server
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.entity.server.Smb

internal fun ServerModelId.toServerId() = ServerId(value)

internal fun ServerFileModelFolder.toServerFolder() =
    ServerFolder(value.first.toServer() to value.second.toFolder())

internal fun FileModel.Folder.toFolder() = Folder(
    serverId = serverModelId.toServerId(),
    name = name,
    parent = parent,
    path = path,
    size = size,
    lastModifier = lastModifier
)

internal fun Smb.Auth.toServerModelAuth(): SmbServerModel.Auth {
    return when (this) {
        Smb.Auth.Guest -> SmbServerModel.Guest
        is Smb.Auth.UsernamePassword -> SmbServerModel.UsernamePassword(domain, username, password)
    }
}

internal fun Server.toServerModel() = when (this) {
    is DeviceStorage -> DeviceStorageModel(ServerModelId(id.value), displayName)
    is Smb -> SmbServerModel(
        id = ServerModelId(id.value),
        name = displayName,
        host = host,
        port = port,
        auth = auth.toServerModelAuth()
    )
}

internal fun ServerModel.toServer() = when (this) {
    is DeviceStorageModel ->
        DeviceStorage(id.toServerId(), name)

    is SmbServerModel -> {
        Smb(
            id = id.toServerId(),
            displayName = name,
            host = host,
            port = port,
            auth = when (val a = auth) {
                SmbServerModel.Guest -> Smb.Auth.Guest
                is SmbServerModel.UsernamePassword -> Smb.Auth.UsernamePassword(
                    "TODO()",
                    a.username,
                    a.password
                )
            }
        )
    }
}

internal fun FavoriteModel.toFavorite(): Favorite {
    return Favorite(FavoriteId(id.value), name, count)
}

internal fun FavoriteBookModel.toFavoriteBook(): FavoriteBook {
    return FavoriteBook(FavoriteId(id.value), serverModelId.toServerId(), filePath)
}

internal fun FavoriteBook.toFavoriteBookModel(): FavoriteBookModel {
    return FavoriteBookModel(FavoriteModelId(id.value), ServerModelId(serverId.value), path)
}

internal fun FileModel.toFile(): File {
    return when (this) {
        is FileModel.File -> BookFile(
            serverModelId.toServerId(),
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
            serverModelId.toServerId(),
            name,
            parent,
            path,
            size,
            lastModifier
        )

        is FileModel.ImageFolder -> BookFolder(
            serverModelId.toServerId(),
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
            serverModelId = ServerModelId(serverId.value),
            name = name,
            parent = parent,
            size = size,
            lastModifier = lastModifier,
            sortIndex = 0
        )

        is BookFile -> FileModel.File(
            path = path,
            serverModelId = ServerModelId(serverId.value),
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
            serverModelId = ServerModelId(serverId.value),
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
