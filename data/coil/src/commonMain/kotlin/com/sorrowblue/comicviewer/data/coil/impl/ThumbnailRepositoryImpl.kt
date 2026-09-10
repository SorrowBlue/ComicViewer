/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.impl

import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.Deferred

@ContributesBinding(AppScope::class)
internal class ThumbnailRepositoryImpl(private val context: PlatformContext) :
    ThumbnailRepository {
    override fun load(fileThumbnail: FileThumbnail): Deferred<Any> {
        val request = ImageRequest
            .Builder(context)
            .data(fileThumbnail)
            .size(ImageSize)
            .build()
        return SingletonImageLoader.get(context).enqueue(request).job
    }
}

private const val ImageSize = 300
