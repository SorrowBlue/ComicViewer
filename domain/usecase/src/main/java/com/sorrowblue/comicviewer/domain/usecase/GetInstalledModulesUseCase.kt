package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.EmptyRequest

abstract class GetInstalledModulesUseCase : UseCase<EmptyRequest, Set<String>, Unit>()
