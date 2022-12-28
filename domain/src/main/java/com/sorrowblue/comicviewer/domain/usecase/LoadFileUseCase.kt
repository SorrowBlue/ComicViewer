package com.sorrowblue.comicviewer.domain.usecase

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.File
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.BaseRequest
import kotlinx.coroutines.flow.Flow

class LoadFileRequest(
    val pagingConfig: PagingConfig,
    val server: Server,
    val bookshelf: Bookshelf
) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class LoadFileUseCase : OneShotUseCase<LoadFileRequest, Flow<PagingData<File>>>()

class PagingQueryFileRequest(
    val pagingConfig: PagingConfig, val server: Server, val query: () -> String
) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class PagingQueryFileUseCase :
    OneShotUseCase<PagingQueryFileRequest, Flow<PagingData<File>>>()
