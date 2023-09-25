package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.model.ScanModel

interface FileScanService {

    suspend fun enqueue(
        fileModel: FileModel,
        scanModel: ScanModel,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>
    ): String
}
