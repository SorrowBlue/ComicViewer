package com.sorrowblue.comicviewer.data.coil

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import coil3.size.Precision
import com.sorrowblue.comicviewer.data.coil.book.BookThumbnailKeyer
import com.sorrowblue.comicviewer.data.coil.collection.CollectionKeyer
import com.sorrowblue.comicviewer.data.coil.di.CoilGraph
import com.sorrowblue.comicviewer.data.coil.folder.FolderThumbnailKeyer
import com.sorrowblue.comicviewer.data.coil.page.BookPageImageKeyer
import com.sorrowblue.comicviewer.framework.common.platformGraph
import logcat.LogPriority
import logcat.logcat

internal open class BaseCoilInitializer {
    fun initialize(platformContext: com.sorrowblue.comicviewer.framework.common.PlatformContext) {
        require(platformContext.platformGraph is CoilGraph.Factory)
        with((platformContext.platformGraph as CoilGraph.Factory).createCoilGraph()) {
            SingletonImageLoader.setSafe { context ->
                ImageLoader(context)
                    .newBuilder()
                    .components {
                        add(BookThumbnailKeyer)
                        add(bookThumbnailFetcher)

                        add(FolderThumbnailKeyer)
                        add(folderThumbnailFetcher)

                        add(BookPageImageKeyer)
                        add(bookPageImageFetcher)

                        add(CollectionKeyer)
                        add(collectionThumbnailFetcher)
                    }.crossfade(true)
                    .precision(Precision.INEXACT)
                    .apply { setup() }
                    .build()
            }
        }
        logcat(LogPriority.INFO) { "Initialized coil." }
    }

    open fun ImageLoader.Builder.setup() {}
}
