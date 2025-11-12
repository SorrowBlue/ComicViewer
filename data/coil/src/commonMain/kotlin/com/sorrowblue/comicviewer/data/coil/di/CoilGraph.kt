package com.sorrowblue.comicviewer.data.coil.di

import coil3.fetch.Fetcher
import com.sorrowblue.comicviewer.data.coil.BookPageImageFetcher
import com.sorrowblue.comicviewer.data.coil.BookThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.CollectionThumbnailFetcher
import com.sorrowblue.comicviewer.data.coil.FolderThumbnailFetcher
import com.sorrowblue.comicviewer.domain.model.BookPageImage
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.FolderThumbnail
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension

@GraphExtension(CoilScope::class)
interface CoilGraph {
    @BookThumbnailFetcher
    val bookThumbnailFetcher: Fetcher.Factory<BookThumbnail>

    @FolderThumbnailFetcher
    val folderThumbnailFetcher: Fetcher.Factory<FolderThumbnail>

    @BookPageImageFetcher
    val bookPageImageFetcher: Fetcher.Factory<BookPageImage>

    @CollectionThumbnailFetcher
    val collectionThumbnailFetcher: Fetcher.Factory<Collection>

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createCoilGraph(): CoilGraph
    }
}
