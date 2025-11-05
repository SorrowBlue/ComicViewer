package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.json.Json

@Composable
fun SortTypeScreenRoot(sortType: SortType, onDismissRequest: () -> Unit) {
    val resultProducer = LocalNavigationResultProducer.current
    SortTypeScreen(
        currentSortType = sortType,
        onFileSortChange = {
            resultProducer.setResult(Json, SortTypeScreenResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest
    )
}
