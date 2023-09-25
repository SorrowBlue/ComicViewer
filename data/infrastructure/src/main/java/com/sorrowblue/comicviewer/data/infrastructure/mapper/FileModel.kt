package com.sorrowblue.comicviewer.data.infrastructure.mapper

import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder

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
            bookshelfModelId = com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId.from(
                file.bookshelfId
            ),
            name = file.name,
            parent = file.parent,
            size = file.size,
            lastModifier = file.lastModifier,
            sortIndex = 0,
        )

        is BookFile -> FileModel.File(
            path = file.path,
            bookshelfModelId = com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId.from(
                file.bookshelfId
            ),
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
            bookshelfModelId = com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModelId.from(
                file.bookshelfId
            ),
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
