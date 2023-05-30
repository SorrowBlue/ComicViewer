package com.sorrowblue.comicviewer.file.info

import androidx.core.net.toUri
import com.sorrowblue.comicviewer.domain.entity.file.File

object FileInfoNavigation {

    fun getDeeplink(file: File) =
        "comicviewer://comicviewer.sorrowblue.com/file_info?server_id=${file.bookshelfId.value}&path=${file.base64Path()}".toUri()
}
