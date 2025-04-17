package com.sorrowblue.comicviewer.data.coil.book

import coil3.key.Keyer
import coil3.request.Options
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail

internal object BookThumbnailKeyer : Keyer<BookThumbnail> {
    override fun key(data: BookThumbnail, options: Options) =
        "book:${data.bookshelfId.value}:${data.path}"
}
