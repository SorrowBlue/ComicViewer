package com.sorrowblue.comicviewer.feature.settings.imagecache

import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ImageCacheScreenScope

@GraphExtension(ImageCacheScreenScope::class)
interface ImageCacheScreenContext : ScreenContext {

    val getBookshelfImageCacheInfoUseCase: GetBookshelfImageCacheInfoUseCase
    val getOtherImageCacheInfoUseCase: GetOtherImageCacheInfoUseCase
    val clearImageCacheUseCase: ClearImageCacheUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createImageCacheScreenContext(): ImageCacheScreenContext
    }
}
