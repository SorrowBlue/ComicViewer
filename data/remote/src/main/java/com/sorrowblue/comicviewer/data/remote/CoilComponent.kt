package com.sorrowblue.comicviewer.data.remote

import android.content.Context
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import com.sorrowblue.comicviewer.data.remote.client.FileClientFactory
import com.sorrowblue.comicviewer.data.remote.reader.FileReaderFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface CoilComponent {

    fun fileClientFactory(): FileClientFactory
    fun fileReaderFactory(): FileReaderFactory
    fun fileModelLocalDataSource(): FileModelLocalDataSource

    companion object {

        fun fileModelLocalDataSource(context: Context) =
            EntryPointAccessors.fromApplication<CoilComponent>(context).fileModelLocalDataSource()

        suspend fun fileReader(context: Context, serverModel: ServerModel, fileModel: FileModel) =
            EntryPointAccessors.fromApplication<CoilComponent>(context).run {
                val fileClient = fileClientFactory().create(serverModel)
                if (fileClient.exists(fileModel)) {
                    fileReaderFactory().create(fileClient, fileModel)
                } else {
                    null
                }
            }
    }
}
