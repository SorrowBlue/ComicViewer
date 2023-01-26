package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.domain.request.BookPageRequest

class BookPageRequestMapper : Mapper<BookPageRequest, BookPageRequestData> {

    override fun map(data: BookPageRequest, options: Options): BookPageRequestData {
        return BookPageRequestData(
            Triple(data.server.toServerModel(), data.book.toFileModel(), data.pageIndex)
        )
    }
}
