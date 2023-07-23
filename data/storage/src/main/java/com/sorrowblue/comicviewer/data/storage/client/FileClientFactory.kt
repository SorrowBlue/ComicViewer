package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel

interface FileClientFactory {

    fun create(bookshelfModel: BookshelfModel): FileClient
}
