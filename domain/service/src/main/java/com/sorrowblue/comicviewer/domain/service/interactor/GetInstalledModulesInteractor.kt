package com.sorrowblue.comicviewer.domain.service.interactor

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class GetInstalledModulesInteractor @Inject constructor(
    private val splitInstallManager: SplitInstallManager,
) : GetInstalledModulesUseCase() {

    override fun run(request: EmptyRequest): Flow<Resource<Set<String>, Unit>> {
        return flowOf(Resource.Success(splitInstallManager.installedModules))
    }
}
