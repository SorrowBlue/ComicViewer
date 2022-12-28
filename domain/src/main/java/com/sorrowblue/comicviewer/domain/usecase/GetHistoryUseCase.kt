package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.Server
import com.sorrowblue.comicviewer.domain.model.EmptyRequest

abstract class GetHistoryUseCase : SingleUseCase<EmptyRequest, Triple<Server, List<Bookshelf>,Int>>()
