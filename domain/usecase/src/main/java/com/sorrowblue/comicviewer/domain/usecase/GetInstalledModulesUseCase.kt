package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.BaseRequest

abstract class GetInstalledModulesUseCase :
    FlowUseCase2<GetInstalledModulesUseCase.Request, Set<String>, Unit>() {

    object Request : BaseRequest
}
