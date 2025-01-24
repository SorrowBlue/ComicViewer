package com.sorrowblue.comicviewer.data.coil

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.fetch.Fetcher
import coil3.request.crossfade
import coil3.size.Precision
import coil3.util.DebugLogger
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import logcat.LogPriority
import logcat.logcat
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Qualifier

@Factory
class CoilInitializer(
    @Qualifier(BookThumbnailFetcher::class)
    private val bookThumbnailFetcher: Fetcher.Factory<BookThumbnail>,

    @Qualifier(FolderThumbnailFetcher::class)
    private val folderThumbnailFetcher: Fetcher.Factory<FolderThumbnail>,
    @Qualifier(BookPageFetcher::class)

    private val bookPageFetcherFactory: Fetcher.Factory<BookPageRequest>,
    @Qualifier(FavoriteFetcher::class)

    private val favoriteThumbnailFetcher: Fetcher.Factory<Favorite>,
) : SingletonImageLoader.Factory {

    fun create() {
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
            .crossfade(true)
            .precision(Precision.INEXACT)
            .logger(DebugLogger())
            .build()
    }
}
