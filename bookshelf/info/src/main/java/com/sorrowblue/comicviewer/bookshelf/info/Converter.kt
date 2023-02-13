package com.sorrowblue.comicviewer.bookshelf.info

import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer

object Converter {

    @JvmStatic
    fun Bookshelf?.toTypeString() = when (this) {
        is InternalStorage -> R.string.bookshelf_info_label_internal_storage
        is SmbServer -> R.string.bookshelf_info_label_smb
        null -> android.R.string.unknownName
    }

}
