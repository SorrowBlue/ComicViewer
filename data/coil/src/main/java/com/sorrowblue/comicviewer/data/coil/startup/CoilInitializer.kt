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
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import javax.inject.Inject
import logcat.LogPriority
import logcat.logcat

internal class CoilInitializer : Initializer<Unit> {

    @Inject
    lateinit var oldFolderThumbnailFetcher: Fetcher.Factory<Folder>

    @Inject
    lateinit var oldBookThumbnailFetcher: Fetcher.Factory<Book>

    @Inject
    lateinit var bookThumbnailFetcher: Fetcher.Factory<BookThumbnail>

    @Inject
    lateinit var folderThumbnailFetcher: Fetcher.Factory<FolderThumbnail>

    @Inject
    lateinit var bookPageFetcherFactory: Fetcher.Factory<BookPageRequest>

    @Inject
    lateinit var favoriteThumbnailFetcher: Fetcher.Factory<Favorite>

    override fun create(context: Context) {
        InitializerEntryPoint.resolve(context).inject(this)
        val imageLoader = ImageLoader(context).newBuilder()
            .components {
                add(oldFolderThumbnailFetcher)
                add(oldBookThumbnailFetcher)
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

        @OptIn(DelicateCoilApi::class)
        SingletonImageLoader.setUnsafe(imageLoader)
        logcat(LogPriority.INFO) { "Initialized coil." }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)
}
