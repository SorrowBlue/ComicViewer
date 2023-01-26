package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.EmptyRequest

abstract class GetNavigationHistoryUseCase : FlowOneUseCase<EmptyRequest, NavigationHistory, Unit>()

