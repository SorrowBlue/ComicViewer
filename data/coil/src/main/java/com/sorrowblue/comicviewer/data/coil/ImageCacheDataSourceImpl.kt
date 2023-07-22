package com.sorrowblue.comicviewer.data.coil

import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import com.sorrowblue.comicviewer.data.datasource.ImageCacheDataSource
import javax.inject.Inject

internal class ImageCacheDataSourceImpl @Inject constructor(
    @ThumbnailDiskCache private val thumbnailDiskCache: dagger.Lazy<DiskCache>,
) : ImageCacheDataSource {

    @OptIn(ExperimentalCoilApi::class)
    override suspend fun deleteThumbnails() {
        thumbnailDiskCache.get().clear()
    }
}
