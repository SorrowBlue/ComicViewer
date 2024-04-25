package com.sorrowblue.comicviewer.domain.service.datasource

interface ImageCacheDataSource {

    suspend fun deleteThumbnails(list: List<String> = emptyList())
}
