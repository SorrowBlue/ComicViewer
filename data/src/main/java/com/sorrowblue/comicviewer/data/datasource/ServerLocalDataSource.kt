package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.ServerFileModelFolder
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import kotlinx.coroutines.flow.Flow

interface ServerLocalDataSource {

    suspend fun create(serverModel: ServerModel): ServerModel

    suspend fun delete(serverModel: ServerModel): Int

    suspend fun get(serverModelId: ServerModelId): ServerModel?

    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<ServerFileModelFolder>>
}
