package com.sorrowblue.comicviewer.data.coil.page

import coil3.key.Keyer
import coil3.request.Options
import com.sorrowblue.comicviewer.domain.model.BookPageImage

internal object BookPageImageKeyer : Keyer<BookPageImage> {
    override fun key(data: BookPageImage, options: Options) =
        "id:${data.book.bookshelfId.value},path:${data.book.path},index:${data.pageIndex}"
}
