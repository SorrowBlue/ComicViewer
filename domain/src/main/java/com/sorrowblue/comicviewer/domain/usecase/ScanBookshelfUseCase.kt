package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class ScanBookshelfUseCase :
    FlowOneUseCase<ScanBookshelfUseCase.Request, String, Unit>() {

    class Request(val folder: IFolder, val scan: Scan) : BaseRequest {
    }
}
