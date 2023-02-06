package com.sorrowblue.comicviewer.data.remote.client

import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel

interface FileClientFactory {

    fun create(bookshelfModel: BookshelfModel): FileClient
}
