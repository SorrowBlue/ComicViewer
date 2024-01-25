package com.sorrowblue.comicviewer.domain.service.repository

interface SplitInstallRepository {
    val installedModules: Set<String>
}