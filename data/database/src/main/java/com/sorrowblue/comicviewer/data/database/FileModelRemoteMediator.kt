package com.sorrowblue.comicviewer.data.database

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.database.entity.File

@OptIn(ExperimentalPagingApi::class)
abstract class FileModelRemoteMediator : RemoteMediator<Int, File>() {
    interface Factory {

        fun create(serverModel: ServerModel, fileModel: FileModel): FileModelRemoteMediator
    }
}
