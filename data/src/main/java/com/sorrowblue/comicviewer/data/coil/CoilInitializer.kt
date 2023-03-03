package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import android.graphics.Bitmap
import androidx.startup.Initializer
import coil.Coil
import coil.fetch.Fetcher
import coil.size.Precision
import com.sorrowblue.comicviewer.data.BookPageRequestMapper
import com.sorrowblue.comicviewer.data.FavoriteMapper
import com.sorrowblue.comicviewer.data.BookMapper
import com.sorrowblue.comicviewer.data.FolderMapper
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.framework.LogcatInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import logcat.LogPriority
import logcat.logcat

internal class CoilInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val imageLoader = Coil.imageLoader(context).newBuilder().components {

            add(BookPageRequestMapper())
            add(MyComponent.bookPageFetcherFactory(context))

            add(FolderMapper())
            add(MyComponent.folderThumbnailFetcher(context))

            add(BookMapper())
            add(MyComponent.bookThumbnailFetcher(context))

            add(FavoriteMapper())
            add(MyComponent.favoriteThumbnailFetcher(context))
        }
            .logger(LogcatLogger())
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

        fun folderThumbnailFetcher(): Fetcher.Factory<FileModel.Folder>
        fun bookThumbnailFetcher(): Fetcher.Factory<FileModel.Book>

        fun bookPageFetcherFactory(): Fetcher.Factory<BookPageRequestData>
        fun favoriteThumbnailFetcher(): Fetcher.Factory<FavoriteModel>

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

