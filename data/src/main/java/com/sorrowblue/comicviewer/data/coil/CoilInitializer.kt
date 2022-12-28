package com.sorrowblue.comicviewer.data.coil

import android.content.Context
import androidx.startup.Initializer
import coil.Coil
import coil.disk.DiskCache
import coil.size.Precision
import com.sorrowblue.comicviewer.data.BookPageRequestMapper
import com.sorrowblue.comicviewer.data.FileThumbnailRequestMapper
import com.sorrowblue.comicviewer.data.common.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.domain.model.FolderThumbnailOrder
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
                add(FileThumbnailRequestMapper())
                add(MyComponent.smbFetcherFactory(context))
                add(
                    MyComponent.factory(context).create {
                        when (MyComponent.settingsCommonRepository(context).displaySettings.first().folderThumbnailOrder) {
                            FolderThumbnailOrder.NAME -> FolderThumbnailOrderModel.NAME
                            FolderThumbnailOrder.MODIFIED -> FolderThumbnailOrderModel.MODIFIED
                            FolderThumbnailOrder.LAST_READ -> FolderThumbnailOrderModel.LAST_READ
                        }
                    }
                )
            }
            .allowRgb565(true)
            .precision(Precision.INEXACT)
            .build()
        Coil.setImageLoader(imageLoader)
    }


    override fun dependencies() = emptyList<Class<out Initializer<*>>>()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    internal interface MyComponent {

        fun factory(): RemoteFileThumbnailFetcherFactory.Factory
        fun smbFetcherFactory(): SmbFetcherFactory
        fun settingsCommonRepository(): SettingsCommonRepository

        companion object {
            fun factory(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).factory()

            fun smbFetcherFactory(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).smbFetcherFactory()

            fun settingsCommonRepository(context: Context) =
                EntryPointAccessors.fromApplication<MyComponent>(context).settingsCommonRepository()
        }
    }

}
