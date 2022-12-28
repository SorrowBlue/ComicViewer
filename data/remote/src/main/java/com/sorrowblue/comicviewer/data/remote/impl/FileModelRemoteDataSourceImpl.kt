package com.sorrowblue.comicviewer.data.remote.impl

import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.datasource.FileModelRemoteDataSource
import com.sorrowblue.comicviewer.data.remote.client.FileClientFactory
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class FileModelRemoteDataSourceImpl @AssistedInject constructor(
    fileClientFactory: FileClientFactory,
    private val fileReaderFactory: FileReaderFactory,
    @Assisted private val serverModel: ServerModel
) : FileModelRemoteDataSource {

    @AssistedFactory
    interface Factory : FileModelRemoteDataSource.Factory {
        override fun create(serverModel: ServerModel): FileModelRemoteDataSourceImpl
    }

    private val fileClient = fileClientFactory.create(serverModel)

    override suspend fun listFiles(
        fileModel: FileModel,
        resolveImageFolder: Boolean,
        filter: (FileModel) -> Boolean
    ): List<FileModel> {
        return fileClient.listFiles(fileModel, resolveImageFolder).filter(filter)
    }

    override suspend fun fileModel(path: String): FileModel {
        return fileClient.current(path)
    }

    override suspend fun exists(path: String): Boolean {
        return fileClient.exists(path)
    }

    override suspend fun count(fileModel: FileModel): Int {
        if (fileClient.exists(fileModel)) {
            val fileReader = fileReaderFactory.create(fileClient, fileModel)
            return fileReader.pageCount().apply {
                fileReader.close()
            }
        } else {
            return 0
        }
    }
}
