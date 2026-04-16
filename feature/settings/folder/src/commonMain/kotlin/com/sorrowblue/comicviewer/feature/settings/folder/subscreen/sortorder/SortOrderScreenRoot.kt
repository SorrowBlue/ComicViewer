package com.sorrowblue.comicviewer.feature.settings.folder.subscreen.sortorder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult

@Composable
internal fun SortOrderScreenRoot(sortType: SortType, onDismissRequest: () -> Unit) {
    val resultProducer = LocalNavigationResultProducer.current
    SortOrderScreen(
        currentSortType = sortType,
        onFileSortChange = {
            resultProducer.setResult(SortOrderScreenResultKey, it)
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest,
    )
}

internal val SortOrderScreenResultKey = SerializableNavigationResultKey(
    serializer = SortType.serializer(),
    resultKey = "SortOrderScreenResultKey",
)
