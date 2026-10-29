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

/**
 * Repository interface for managing "Read Later" file entries.
 */
interface ReadLaterFileRepository {
    /**
     * Adds or updates a [file] in the Read Later list.
     */
    suspend fun updateOrAdd(file: ReadLaterFile): Resource<ReadLaterFile, Resource.SystemError>

    /**
     * Removes a [file] from the Read Later list.
     */
    suspend fun delete(file: ReadLaterFile): Resource<Unit, Resource.SystemError>

    /**
     * Clears all entries from the Read Later list.
     */
    suspend fun deleteAll(): Resource<Unit, Resource.SystemError>

    /**
     * Observes whether [file] is registered in the Read Later list.
     */
    fun exists(file: ReadLaterFile): Resource<Flow<Boolean>, Resource.SystemError>

    /**
     * Observes paginated [File] items in the Read Later list.
     */
    fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<File>>
}
