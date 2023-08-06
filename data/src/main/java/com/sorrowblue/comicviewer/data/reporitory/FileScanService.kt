package com.sorrowblue.comicviewer.data.reporitory

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.model.ScanModel

interface FileScanService {

    suspend fun enqueue(
        fileModel: FileModel,
        scanModel: ScanModel,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>
    ): String
}
