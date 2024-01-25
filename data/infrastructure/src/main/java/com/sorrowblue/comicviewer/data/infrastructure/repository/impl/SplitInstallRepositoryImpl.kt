package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import com.google.android.play.core.splitinstall.SplitInstallManager
import com.sorrowblue.comicviewer.domain.service.repository.SplitInstallRepository
import javax.inject.Inject

internal class SplitInstallRepositoryImpl @Inject constructor(
    private val splitInstallManager: SplitInstallManager,
) : SplitInstallRepository {
    override val installedModules: Set<String>
        get() = splitInstallManager.installedModules
}