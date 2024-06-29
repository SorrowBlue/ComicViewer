package com.sorrowblue.comicviewer.bookshelf.component

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.feature.bookshelf.R

object BookshelfConverter {

    @JvmStatic
    fun displayName(bookshelf: Bookshelf, folder: IFolder) = when (bookshelf) {
        is InternalStorage -> bookshelf.displayName.ifEmpty { folder.name }
        is SmbServer -> bookshelf.displayName.ifEmpty { bookshelf.host }
        ShareContents -> bookshelf.displayName.ifEmpty { bookshelf.displayName }
    }

    @JvmStatic
    fun Bookshelf?.source() = when (this) {
        is InternalStorage -> R.string.bookshelf_info_label_internal_storage
        is SmbServer -> R.string.bookshelf_info_label_smb
        null -> android.R.string.unknownName
        ShareContents -> 0
    }
}
