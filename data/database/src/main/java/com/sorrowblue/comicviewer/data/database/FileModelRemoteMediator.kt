package com.sorrowblue.comicviewer.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.database.entity.FileWithCount
import com.sorrowblue.comicviewer.data.model.FileModel
import com.sorrowblue.comicviewer.data.model.bookshelf.BookshelfModel

@OptIn(ExperimentalPagingApi::class)
abstract class FileModelRemoteMediator : RemoteMediator<Int, FileWithCount>() {
    interface Factory {

        fun create(bookshelfModel: BookshelfModel, fileModel: FileModel): FileModelRemoteMediator
    }
}
