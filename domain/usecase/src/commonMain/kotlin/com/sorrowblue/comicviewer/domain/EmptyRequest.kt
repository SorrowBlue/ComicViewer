/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain

import com.sorrowblue.comicviewer.domain.usecase.UseCase

data object EmptyRequest : BaseRequest, UseCase.Request
