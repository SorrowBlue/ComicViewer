package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class GetInstalledModulesInteractor : GetInstalledModulesUseCase() {

    override fun run(request: EmptyRequest): Flow<Resource<Set<String>, Unit>> {
        return flowOf(Resource.Error(Unit))
    }
}
