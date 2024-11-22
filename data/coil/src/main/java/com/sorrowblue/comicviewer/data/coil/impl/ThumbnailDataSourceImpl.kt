package com.sorrowblue.comicviewer.data.coil.impl

import android.content.Context
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.service.datasource.ThumbnailDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Deferred

internal class ThumbnailDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ThumbnailDataSource {

    override fun load(fileThumbnail: FileThumbnail): Deferred<Any> {
        val request = ImageRequest.Builder(context)
            .data(fileThumbnail)
            .size(300)
            .build()
        return SingletonImageLoader.get(context).enqueue(request).job
    }
}
