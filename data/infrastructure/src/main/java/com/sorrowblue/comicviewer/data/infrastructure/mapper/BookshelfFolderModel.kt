package com.sorrowblue.comicviewer.data.infrastructure.mapper

import com.sorrowblue.comicviewer.data.model.BookshelfFolderModel
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.Folder

internal fun BookshelfFolderModel.toBookshelfFolder() =
    BookshelfFolder(value.first.toBookshelf() to value.second.toFolder())

internal fun FileModel.Folder.toFolder() = Folder(
    bookshelfId = bookshelfModelId.toBookshelfId(),
    name = name,
    parent = parent,
    path = path,
    size = size,
    lastModifier = lastModifier,
    count = count
)
