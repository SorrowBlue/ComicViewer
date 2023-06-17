package com.sorrowblue.comicviewer.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.database.entity.FileWithCount

@OptIn(ExperimentalPagingApi::class)
abstract class FileModelRemoteMediator : RemoteMediator<Int, FileWithCount>() {
    interface Factory {

        fun create(bookshelfModel: BookshelfModel, fileModel: FileModel): FileModelRemoteMediator
    }
}
