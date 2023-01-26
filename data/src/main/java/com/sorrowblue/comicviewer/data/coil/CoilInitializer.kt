package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.BitmapCompat
import androidx.startup.Initializer
import coil.Coil
import coil.fetch.Fetcher
import coil.size.Precision
import com.sorrowblue.comicviewer.data.BookPageRequestMapper
import com.sorrowblue.comicviewer.data.FavoriteMapper
import com.sorrowblue.comicviewer.data.FileThumbnailRequestMapper
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.common.FavoriteBookModel
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.ServerFileModel
import com.sorrowblue.comicviewer.domain.entity.settings.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

internal class CoilInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val imageLoader = Coil.imageLoader(context).newBuilder()
            .components {

                add(BookPageRequestMapper())
                add(MyComponent.bookPageFetcherFactory(context))

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
        fun bookThumbnailFetcher(): Fetcher.Factory<ServerFileModel>
        fun favoriteThumbnailFetcher(): Fetcher.Factory<FavoriteModel>

        companion object {
            fun bookThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).bookThumbnailFetcher()

            fun bookPageFetcherFactory(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).bookPageFetcherFactory()

            fun favoriteThumbnailFetcher(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).favoriteThumbnailFetcher()
        }
    }

}
