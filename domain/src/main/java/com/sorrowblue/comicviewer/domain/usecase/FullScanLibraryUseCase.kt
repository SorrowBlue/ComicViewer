package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.model.ScanType

abstract class FullScanLibraryUseCase :
    FlowOneUseCase<FullScanLibraryUseCase.Request, String, Unit>() {

    class Request(val folder: Folder, val scanType: ScanType) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
