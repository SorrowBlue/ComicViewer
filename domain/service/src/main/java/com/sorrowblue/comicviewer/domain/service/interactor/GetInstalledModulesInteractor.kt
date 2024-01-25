package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.repository.SplitInstallRepository
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class GetInstalledModulesInteractor @Inject constructor(
    private val repository: SplitInstallRepository,
) : GetInstalledModulesUseCase() {

    override fun run(request: Request): Flow<Result<Set<String>, Unit>> {
        return flowOf(Result.Success(repository.installedModules))
    }
}