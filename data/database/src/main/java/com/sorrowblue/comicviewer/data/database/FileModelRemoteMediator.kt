package com.sorrowblue.comicviewer.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.database.entity.File

@OptIn(ExperimentalPagingApi::class)
abstract class FileModelRemoteMediator : RemoteMediator<Int, File>() {
    interface Factory {

        fun create(bookshelfModel: BookshelfModel, fileModel: FileModel): FileModelRemoteMediator
    }
}
