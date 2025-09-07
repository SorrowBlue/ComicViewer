package com.sorrowblue.comicviewer.data.coil.impl

import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.ThumbnailDataSource
import kotlinx.coroutines.Deferred
import org.koin.core.annotation.Single

@Single
internal class ThumbnailDataSourceImpl(
    private val context: PlatformContext,
) : ThumbnailDataSource {

    override fun load(fileThumbnail: FileThumbnail): Deferred<Any> {
        val request = ImageRequest.Builder(context)
            .data(fileThumbnail)
            .size(300)
            .build()
        return SingletonImageLoader.get(context).enqueue(request).job
    }
}
