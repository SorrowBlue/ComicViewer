package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import android.graphics.Bitmap
import androidx.startup.Initializer
import coil.Coil
import coil.fetch.Fetcher
import coil.size.Precision
import com.sorrowblue.comicviewer.data.BookPageRequestMapper
import com.sorrowblue.comicviewer.data.FavoriteMapper
import com.sorrowblue.comicviewer.data.FileMapper
import com.sorrowblue.comicviewer.data.FileThumbnailRequestMapper
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfFileModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

internal class CoilInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val imageLoader = Coil.imageLoader(context).newBuilder()
            .components {

                add(BookPageRequestMapper())
                add(MyComponent.bookPageFetcherFactory(context))

                add(FileMapper())
                add(MyComponent.fileThumbnailFetcher(context))

                add(FileThumbnailRequestMapper())
                add(MyComponent.bookThumbnailFetcher(context))

                add(FavoriteMapper())
                add(MyComponent.favoriteThumbnailFetcher(context))
            }
            .allowRgb565(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .precision(Precision.INEXACT)
            .build()
        Coil.setImageLoader(imageLoader)
    }


    override fun dependencies() = emptyList<Class<out Initializer<*>>>()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface MyComponent {

        fun bookPageFetcherFactory(): Fetcher.Factory<BookPageRequestData>
        fun fileThumbnailFetcher(): Fetcher.Factory<FileModel>
        fun bookThumbnailFetcher(): Fetcher.Factory<BookshelfFileModel>
        fun favoriteThumbnailFetcher(): Fetcher.Factory<FavoriteModel>

        companion object {
            fun bookThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).bookThumbnailFetcher()

            fun fileThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).fileThumbnailFetcher()

            fun bookPageFetcherFactory(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).bookPageFetcherFactory()

            fun favoriteThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).favoriteThumbnailFetcher()
        }
    }

}
