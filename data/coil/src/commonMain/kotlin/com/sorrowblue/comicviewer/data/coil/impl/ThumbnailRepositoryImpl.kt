/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.coil.impl

import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.repository.ThumbnailRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ThumbnailRepositoryImpl(private val context: PlatformContext) :
    ThumbnailRepository {
    override suspend fun load(fileThumbnail: FileThumbnail): Resource<Unit, Resource.SystemError> {
        val request = ImageRequest
            .Builder(context)
            .data(fileThumbnail)
            .size(ImageSize)
            .build()
        return when (val result = SingletonImageLoader.get(context).execute(request)) {
            is SuccessResult -> Resource.Success(Unit)
            is ErrorResult -> Resource.Error(Resource.SystemError(result.throwable))
        }
    }
}

private const val ImageSize = 300
