/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.readlater.ReadLaterFile
import kotlinx.coroutines.flow.Flow

interface ReadLaterFileRepository {
    suspend fun updateOrAdd(file: ReadLaterFile): Resource<ReadLaterFile, Resource.SystemError>

    suspend fun delete(file: ReadLaterFile): Resource<Unit, Resource.SystemError>

    suspend fun deleteAll(): Resource<Unit, Resource.SystemError>

    fun exists(file: ReadLaterFile): Resource<Flow<Boolean>, Resource.SystemError>

    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>>
}
