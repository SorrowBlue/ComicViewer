package com.sorrowblue.comicviewer.domain.service.interactor

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class GetInstalledModulesInteractor @Inject constructor(
    private val splitInstallManager: SplitInstallManager,
) : GetInstalledModulesUseCase() {

    override fun run(request: Request): Flow<Result<Set<String>, Unit>> {
        return flowOf(Result.Success(splitInstallManager.installedModules))
    }
}
