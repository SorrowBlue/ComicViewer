package com.sorrowblue.comicviewer.data

import com.sorrowblue.comicviewer.data.common.DeviceStorageModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerFileModelFolder
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SmbServerModel
import com.sorrowblue.comicviewer.domain.entity.BookFile
import com.sorrowblue.comicviewer.domain.entity.BookFolder
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import com.sorrowblue.comicviewer.domain.entity.ServerId

internal fun ServerModelId.toServerId() = ServerId(value)

internal fun ServerFileModelFolder.toServerBookshelf() =
    ServerBookshelf(value.first.toServer() to value.second.toBookshelf())

internal fun FileModel.Folder.toBookshelf() =
    Bookshelf(
        serverId = serverModelId.toServerId(),
        name = name,
        parent = parent,
        path = path,
        size = size,
        lastModifier = lastModifier
    )

internal fun Server.Smb.Auth.toServerModelAuth(): SmbServerModel.Auth {
    return when (this) {
        Server.Smb.Auth.Guest -> SmbServerModel.Guest
        is Server.Smb.Auth.UsernamePassword -> SmbServerModel.UsernamePassword(username, password)
    }
}

internal fun Server.toServerModel(): ServerModel {
    return when (this) {
        is Server.DeviceStorage -> DeviceStorageModel(ServerModelId(id.value), displayName)
        is Server.Smb -> SmbServerModel(
            id = ServerModelId(id.value),
            name = displayName,
            host = host,
            port = port,
            auth = auth.toServerModelAuth()
        )
    }
}

internal fun ServerModel.toServer(): Server {
    return when (this) {
        is DeviceStorageModel ->
            Server.DeviceStorage(id.toServerId(), name)
        is SmbServerModel -> {
            Server.Smb(
                id = id.toServerId(),
                displayName = name,
                host = host,
                port = port,
                auth = when (val a = auth) {
                    SmbServerModel.Guest -> Server.Smb.Auth.Guest
                    is SmbServerModel.UsernamePassword -> Server.Smb.Auth.UsernamePassword(
                        "TODO()",
                        a.username,
                        a.password
                    )
                }
            )
        }
    }
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
        is FileModel.Folder -> Bookshelf(
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
        is Bookshelf -> FileModel.Folder(
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
