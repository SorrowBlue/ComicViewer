package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.file.Bookshelf
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.model.ScanType

abstract class FullScanLibraryUseCase :
    FlowOneUseCase<FullScanLibraryUseCase.Request, String, Unit>() {

    class Request(val bookshelf: Bookshelf, val scanType: ScanType) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
