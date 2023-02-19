package com.sorrowblue.comicviewer.bookshelf

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.entity.file.Folder

object BookshelfConverter {

    @JvmStatic
    fun displayName(bookshelf: Bookshelf, folder: Folder) = when(bookshelf) {
        is InternalStorage -> bookshelf.displayName.ifEmpty { folder.name }
        is SmbServer -> bookshelf.displayName.ifEmpty { bookshelf.host }
    }

    @JvmStatic
    fun Bookshelf?.source() = when (this) {
        is InternalStorage -> R.string.bookshelf_info_label_internal_storage
        is SmbServer -> R.string.bookshelf_info_label_smb
        null -> android.R.string.unknownName
    }
}
