package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.EmptyRequest

abstract class GetNavigationHistoryUseCase : FlowOneUseCase<EmptyRequest, NavigationHistory, Unit>()
