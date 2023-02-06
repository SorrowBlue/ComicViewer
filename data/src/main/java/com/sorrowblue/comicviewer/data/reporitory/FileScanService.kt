package com.sorrowblue.comicviewer.data.reporitory

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.ScanTypeModel

interface FileScanService {

    suspend fun enqueue(
        fileModel: FileModel,
        scanTypeModel: ScanTypeModel,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>
    ): String
}
