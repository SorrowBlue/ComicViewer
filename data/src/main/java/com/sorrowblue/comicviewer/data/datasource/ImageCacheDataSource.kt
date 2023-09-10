package com.sorrowblue.comicviewer.data.datasource

interface ImageCacheDataSource {

    suspend fun deleteThumbnails(list: List<String> = emptyList())
}