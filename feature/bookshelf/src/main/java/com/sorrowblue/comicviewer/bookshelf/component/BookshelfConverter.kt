package com.sorrowblue.comicviewer.bookshelf.component

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.IFolder

object BookshelfConverter {

    @JvmStatic
    fun displayName(bookshelf: Bookshelf, folder: IFolder) = when (bookshelf) {
        is InternalStorage -> bookshelf.displayName.ifEmpty { folder.name }
        is SmbServer -> bookshelf.displayName.ifEmpty { bookshelf.host }
        ShareContents -> bookshelf.displayName.ifEmpty { bookshelf.displayName }
    }
}
