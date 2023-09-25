package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModel

interface FileClientFactory {

    fun create(bookshelfModel: BookshelfModel): FileClient
}
