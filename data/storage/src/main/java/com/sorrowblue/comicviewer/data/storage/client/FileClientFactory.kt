package com.sorrowblue.comicviewer.data.storage.client

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf

interface FileClientFactory {

    fun create(bookshelf: Bookshelf): FileClient
}
