package com.sorrowblue.comicviewer.domain.service.datasource

import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import kotlinx.coroutines.Deferred

interface ThumbnailDataSource {
    fun load(fileThumbnail: FileThumbnail): Deferred<Any>
}
