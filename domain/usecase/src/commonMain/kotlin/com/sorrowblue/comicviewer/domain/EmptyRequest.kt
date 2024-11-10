package com.sorrowblue.comicviewer.domain

import com.sorrowblue.comicviewer.domain.usecase.UseCase

data object EmptyRequest : BaseRequest, UseCase.Request
