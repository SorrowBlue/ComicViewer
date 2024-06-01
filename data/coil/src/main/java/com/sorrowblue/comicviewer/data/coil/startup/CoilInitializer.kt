package com.sorrowblue.comicviewer.data.coil.startup

import android.content.Context
import android.graphics.Bitmap
import androidx.startup.Initializer
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.annotation.DelicateCoilApi
import coil3.fetch.Fetcher
import coil3.request.allowRgb565
import coil3.request.bitmapConfig
import coil3.request.crossfade
import coil3.size.Precision
import coil3.util.DebugLogger
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import javax.inject.Inject
import logcat.LogPriority
import logcat.logcat

internal class CoilInitializer : Initializer<Unit> {

    @Inject
    lateinit var folderThumbnailFetcher: Fetcher.Factory<Folder>

    @Inject
    lateinit var bookThumbnailFetcher: Fetcher.Factory<Book>

    @Inject
    lateinit var bookPageFetcherFactory: Fetcher.Factory<BookPageRequest>

    @Inject
    lateinit var favoriteThumbnailFetcher: Fetcher.Factory<Favorite>

    override fun create(context: Context) {
        InitializerEntryPoint.resolve<Any?>(context).inject(this)
        val imageLoader = ImageLoader(context).newBuilder()
            .components {
                add(folderThumbnailFetcher)
                add(bookThumbnailFetcher)
                add(bookPageFetcherFactory)
                add(favoriteThumbnailFetcher)
            }
            .allowRgb565(true)
            .crossfade(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .precision(Precision.INEXACT)
            .logger(DebugLogger())
            .build()

        @OptIn(DelicateCoilApi::class)
        SingletonImageLoader.setUnsafe(imageLoader)
        logcat(LogPriority.INFO) { "Initialized coil." }
    }

    override fun dependencies() =
        listOf(LogcatInitializer::class.java)
}
