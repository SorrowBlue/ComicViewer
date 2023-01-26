package com.sorrowblue.comicviewer.data.database.entity

import com.sorrowblue.comicviewer.data.common.FavoriteBookModel
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FileModel

internal fun FavoriteBookModel.toFavoriteBook() =
    FavoriteBook(id.value, serverModelId.value, filePath)

internal fun FavoriteModel.toFavorite() =
    Favorite(id.value, name)

internal fun FileModel.toFile(): File {
    return when (this) {
        is FileModel.File -> File(
            path = path,
            serverId = serverModelId.value,
            name = name,
            parent = parent,
            size = size,
            lastModified = lastModifier,
            fileType = File.Type.FILE,
            sortIndex = sortIndex,
            info = FileInfo(cacheKey, totalPageCount),
            history = FileHistory(lastReadPage, lastRead)
        )
        is FileModel.Folder -> File(
            path = path,
            serverId = serverModelId.value,
            name = name,
            parent = parent,
            size = size,
            lastModified = lastModifier,
            fileType = File.Type.FOLDER,
            sortIndex = sortIndex,
            info = FileInfo("", 0),
            history = FileHistory(0, 0)
        )
        is FileModel.ImageFolder -> File(
            path = path,
            serverId = serverModelId.value,
            name = name,
            parent = parent,
            size = size,
            lastModified = lastModifier,
            fileType = File.Type.IMAGE_FOLDER,
            sortIndex = sortIndex,
            info = FileInfo(cacheKey, totalPageCount),
            history = FileHistory(lastReadPage, lastRead)
        )
    }
}
