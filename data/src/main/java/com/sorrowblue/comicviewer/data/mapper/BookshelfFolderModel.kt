package com.sorrowblue.comicviewer.data.mapper

import com.sorrowblue.comicviewer.data.common.BookshelfFolderModel
import com.sorrowblue.comicviewer.data.common.FileModel
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
