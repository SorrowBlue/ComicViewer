package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import android.graphics.Bitmap
import androidx.startup.Initializer
import coil.Coil
import coil.fetch.Fetcher
import coil.size.Precision
import com.sorrowblue.comicviewer.domain.model.BookPageRequest
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import logcat.LogPriority
import logcat.logcat

internal class CoilInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val imageLoader = Coil.imageLoader(context).newBuilder().components {

            add(MyComponent.bookPageFetcherFactory(context))

            add(MyComponent.folderThumbnailFetcher(context))

            add(MyComponent.bookThumbnailFetcher(context))

            add(MyComponent.favoriteThumbnailFetcher(context))
        }
            .allowRgb565(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .precision(Precision.INEXACT)
            .build()
        Coil.setImageLoader(imageLoader)
        logcat(LogPriority.INFO) { "Initialize coil." }
    }


    override fun dependencies() = listOf(LogcatInitializer::class.java)

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface MyComponent {

        fun folderThumbnailFetcher(): Fetcher.Factory<Folder>
        fun bookThumbnailFetcher(): Fetcher.Factory<Book>

        fun bookPageFetcherFactory(): Fetcher.Factory<BookPageRequest>
        fun favoriteThumbnailFetcher(): Fetcher.Factory<Favorite>

        companion object {
            fun folderThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).folderThumbnailFetcher()

            fun bookThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).bookThumbnailFetcher()

            fun bookPageFetcherFactory(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).bookPageFetcherFactory()

            fun favoriteThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).favoriteThumbnailFetcher()
        }
    }

}

