package com.sorrowblue.comicviewer.data

import coil.map.Mapper
import coil.request.Options
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.domain.entity.file.Book

internal class BookMapper : Mapper<Book, FileModel.Book> {

    override fun map(data: Book, options: Options) = data.toFileModel() as FileModel.Book
}

