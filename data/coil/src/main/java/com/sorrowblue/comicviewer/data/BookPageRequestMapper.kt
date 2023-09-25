package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.BookPageRequestData
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.mapper.from
import com.sorrowblue.comicviewer.domain.model.BookPageRequest

class BookPageRequestMapper : Mapper<BookPageRequest, BookPageRequestData> {

    override fun map(data: BookPageRequest, options: Options): BookPageRequestData {
        return BookPageRequestData(
            FileModel.from(data.book) to data.pageIndex
        )
    }
}
