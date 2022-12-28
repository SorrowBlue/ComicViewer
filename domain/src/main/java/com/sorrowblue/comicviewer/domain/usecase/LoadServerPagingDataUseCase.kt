package com.sorrowblue.comicviewer.domain.usecase

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BaseRequest
import com.sorrowblue.comicviewer.domain.entity.ServerBookshelf
import kotlinx.coroutines.flow.Flow

abstract class LoadServerPagingDataUseCase :
    OneShotUseCase<LoadServerPagingDataUseCase.RequestData, Flow<PagingData<ServerBookshelf>>>() {

    class RequestData(val pagingConfig: PagingConfig) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }

}
