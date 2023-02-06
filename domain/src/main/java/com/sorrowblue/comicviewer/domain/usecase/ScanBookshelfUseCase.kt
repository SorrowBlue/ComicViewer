package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.model.ScanType
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class ScanBookshelfUseCase :
    FlowOneUseCase<ScanBookshelfUseCase.Request, String, Unit>() {

    class Request(val folder: Folder, val scanType: ScanType) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
