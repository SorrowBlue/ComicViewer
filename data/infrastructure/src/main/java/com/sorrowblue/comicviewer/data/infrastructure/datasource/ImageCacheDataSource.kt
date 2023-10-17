package com.sorrowblue.comicviewer.data.infrastructure.datasource

interface ImageCacheDataSource {

    suspend fun deleteThumbnails(list: List<String> = emptyList())
}
