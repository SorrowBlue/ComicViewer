package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.file.File

interface FileScanService {

    suspend fun enqueue(
        file: File,
        scan: Scan,
        resolveImageFolder: Boolean,
        supportExtensions: List<String>,
    ): String
}
