/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.search

import com.sorrowblue.comicviewer.domain.model.SearchCondition
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchScreenUiState(val searchCondition: SearchCondition = SearchCondition())
