package com.sorrowblue.comicviewer.data.datasource

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModel

interface FileModelRemoteDataSource {

    interface Factory {
        fun create(serverModel: ServerModel): FileModelRemoteDataSource
    }

    suspend fun exists(path: String): Boolean
    suspend fun listFiles(
        fileModel: FileModel,
        resolveImageFolder: Boolean = false,
        filter: (FileModel) -> Boolean
    ): List<FileModel>

    suspend fun fileModel(path: String): FileModel

    suspend fun count(fileModel: FileModel): Int

}
