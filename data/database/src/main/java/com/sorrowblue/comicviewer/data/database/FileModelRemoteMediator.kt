package com.sorrowblue.comicviewer.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.database.entity.FileWithCountEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.File

@OptIn(ExperimentalPagingApi::class)
abstract class FileModelRemoteMediator : RemoteMediator<Int, FileWithCountEntity>() {
    interface Factory {

        fun create(bookshelf: Bookshelf, file: File): FileModelRemoteMediator
    }
}
