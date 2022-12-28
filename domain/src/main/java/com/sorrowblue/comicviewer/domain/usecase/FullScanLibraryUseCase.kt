package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.BaseRequest
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.entity.Bookshelf

class FullScanLibraryRequest(val bookshelf: Bookshelf, val scanType: ScanType) : BaseRequest {
    override fun validate(): Boolean {
        return true
    }
}

abstract class FullScanLibraryUseCase : OneTimeUseCase2<FullScanLibraryRequest, String, Unit>()
