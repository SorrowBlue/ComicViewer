package com.sorrowblue.comicviewer.data.common

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel

@JvmInline
value class BookshelfFolderModel(val value: Pair<BookshelfModel, FileModel.Folder>)
