package com.sorrowblue.comicviewer.data.mapper

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.BookFolder
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder

fun FileModel.toFile(): File {
    return when (this) {
        is FileModel.File -> BookFile(
            bookshelfModelId.toBookshelfId(),
            name,
            parent,
            path,
            size,
            lastModifier,
            cacheKey,
            lastReadPage,
            totalPageCount,
            lastReading
        )

        is FileModel.Folder -> Folder(
            bookshelfModelId.toBookshelfId(),
            name,
            parent,
            path,
            size,
            lastModifier,
            count = count
        )

        is FileModel.ImageFolder -> BookFolder(
            bookshelfModelId.toBookshelfId(),
            name,
            parent,
            path,
            size,
            lastModifier,
            cacheKey,
            lastReadPage,
            totalPageCount,
            lastReading,
            count = count
        )
    }
}

fun FileModel.Companion.from(file: File): FileModel {
    return when (file) {
        is Folder -> FileModel.Folder(
            path = file.path,
            bookshelfModelId = BookshelfModelId.from(file.bookshelfId),
            name = file.name,
            parent = file.parent,
            size = file.size,
            lastModifier = file.lastModifier,
            sortIndex = 0,
        )

        is BookFile -> FileModel.File(
            path = file.path,
            bookshelfModelId = BookshelfModelId.from(file.bookshelfId),
            name = file.name,
            parent = file.parent,
            size = file.size,
            lastModifier = file.lastModifier,
            sortIndex = 0,
            cacheKey = file.cacheKey,
            totalPageCount = file.totalPageCount,
            lastReadPage = file.lastPageRead,
            lastReading = file.lastReadTime
        )

        is BookFolder -> FileModel.ImageFolder(
            path = file.path,
            bookshelfModelId = BookshelfModelId.from(file.bookshelfId),
            name = file.name,
            parent = file.parent,
            size = file.size,
            lastModifier = file.lastModifier,
            sortIndex = 0,
            cacheKey = file.cacheKey,
            totalPageCount = file.totalPageCount,
            lastReadPage = file.lastPageRead,
            lastReading = file.lastReadTime
        )
    }
}
