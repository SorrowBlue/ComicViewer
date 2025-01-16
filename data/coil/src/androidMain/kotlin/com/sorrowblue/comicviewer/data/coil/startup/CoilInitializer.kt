package com.sorrowblue.comicviewer.data.coil.startup

import android.content.Context
import android.graphics.Bitmap
import androidx.startup.Initializer
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.fetch.Fetcher
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.request.crossfade
import coil3.size.Precision
import coil3.util.DebugLogger
import com.sorrowblue.comicviewer.data.coil.BookPageFetcher
import com.sorrowblue.comicviewer.data.coil.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.FavoriteFetcher
import com.sorrowblue.comicviewer.data.coil.FolderThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.getValue
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import logcat.LogPriority
import logcat.logcat
import org.koin.androix.startup.KoinInitializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

internal class CoilInitializer : Initializer<Unit>, KoinComponent, SingletonImageLoader.Factory {

    private val bookThumbnailFetcher: Fetcher.Factory<BookThumbnail> by inject(named<BookThumbnailFetcher>())

    private val folderThumbnailFetcher: Fetcher.Factory<FolderThumbnail> by inject(named<FolderThumbnailFetcher>())

    private val bookPageFetcherFactory: Fetcher.Factory<BookPageRequest> by inject(named<BookPageFetcher>())

    private val favoriteThumbnailFetcher: Fetcher.Factory<Favorite> by inject(named<FavoriteFetcher>())

    override fun create(context: Context) {
        SingletonImageLoader.setSafe(this)
        logcat(LogPriority.INFO) { "Initialized coil." }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader(context).newBuilder()
            .components {
                add(bookThumbnailFetcher)
                add(folderThumbnailFetcher)
                add(bookPageFetcherFactory)
                add(favoriteThumbnailFetcher)
            }
            .allowRgb565(true)
            .crossfade(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .precision(Precision.INEXACT)
            .logger(DebugLogger())
            .build()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(LogcatInitializer::class.java, KoinInitializer::class.java)
    }
}
