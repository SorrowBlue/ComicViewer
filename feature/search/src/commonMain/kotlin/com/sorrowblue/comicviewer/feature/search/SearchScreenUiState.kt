package com.sorrowblue.comicviewer.feature.search

import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.feature.search.section.SearchListUiState
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchScreenUiState(
    val searchCondition: SearchCondition = SearchCondition(),
    val searchContentsUiState: SearchListUiState = SearchListUiState(),
)
