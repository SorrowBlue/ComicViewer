package com.sorrowblue.comicviewer.data.model

import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModel

@JvmInline
value class BookshelfFolderModel(val value: Pair<BookshelfModel, FileModel.Folder>)
