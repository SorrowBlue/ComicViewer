package com.sorrowblue.comicviewer.data.coil

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.fetch.Fetcher
import coil3.request.crossfade
import coil3.size.Precision
import coil3.util.DebugLogger
import com.sorrowblue.comicviewer.data.coil.book.BookThumbnailKeyer
import com.sorrowblue.comicviewer.data.coil.favorite.FavoriteKeyer
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailKeyer
import com.sorrowblue.comicviewer.data.coil.page.BookPageImageKeyer
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import logcat.LogPriority
import logcat.logcat
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

internal abstract class BaseCoilInitializer : KoinComponent, SingletonImageLoader.Factory {

    @Suppress("UndeclaredKoinUsage")
    private val bookThumbnailFetcher: Fetcher.Factory<BookThumbnail> by inject(named<BookThumbnailFetcher>())

    @Suppress("UndeclaredKoinUsage")
    private val folderThumbnailFetcher: Fetcher.Factory<FolderThumbnail> by inject(named<FolderThumbnailFetcher>())

    @Suppress("UndeclaredKoinUsage")
    private val bookPageImageFetcher: Fetcher.Factory<BookPageImage> by inject(named<BookPageImageFetcher>())

    @Suppress("UndeclaredKoinUsage")
    private val favoriteThumbnailFetcher: Fetcher.Factory<Favorite> by inject(named<FavoriteFetcher>())

    fun initialize() {
        SingletonImageLoader.setSafe(this)
        logcat(LogPriority.INFO) { "Initialized coil." }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader(context).newBuilder()
            .components {
                add(bookThumbnailFetcher)
                add(folderThumbnailFetcher)
                add(bookPageImageFetcher)
                add(favoriteThumbnailFetcher)
                add(BookThumbnailKeyer)
                add(FavoriteKeyer)
                add(FolderThumbnailKeyer)
                add(BookPageImageKeyer)
            }
            .crossfade(true)
            .precision(Precision.INEXACT)
            .logger(DebugLogger())
            .apply { setup() }
            .build()
    }

    open fun ImageLoader.Builder.setup() {}
}
