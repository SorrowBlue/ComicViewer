package com.sorrowblue.comicviewer.domain.service.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.ReadLaterFile
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import kotlinx.coroutines.flow.Flow

interface ReadLaterFileLocalDataSource {
    suspend fun updateOrAdd(file: ReadLaterFile): Resource<ReadLaterFile, Resource.SystemError>

    suspend fun delete(file: ReadLaterFile): Resource<Unit, Resource.SystemError>

    suspend fun deleteAll(): Resource<Unit, Resource.SystemError>

    fun exists(file: ReadLaterFile): Resource<Flow<Boolean>, Resource.SystemError>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>>
}
