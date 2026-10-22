/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.preview.fake

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

fun <T : Any> PagingData.Companion.flowData(size: Int = 20, init: (Int) -> T): Flow<PagingData<T>> =
    MutableStateFlow(from(data = List(size, init)))
