package com.sorrowblue.comicviewer.bookshelf.info

import com.sorrowblue.comicviewer.domain.entity.server.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.server.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.server.SmbServer

object Converter {

    @JvmStatic
    fun Bookshelf?.toTypeString() = when (this) {
        is InternalStorage -> R.string.bookshelf_info_label_internal_storage
        is SmbServer -> R.string.bookshelf_info_label_smb
        null -> android.R.string.unknownName
    }

}
