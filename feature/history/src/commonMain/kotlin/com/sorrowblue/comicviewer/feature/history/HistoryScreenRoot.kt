package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.file.Book
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultConsumer
import io.github.irgaly.navigation3.resultstate.SerializedNavigationResult
import io.github.irgaly.navigation3.resultstate.getResultState
import kotlinx.serialization.json.Json

@Composable
context(context: HistoryScreenContext)
internal fun HistoryScreenRoot(
    onDeleteAllClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onBookInfoClick: (Book) -> Unit,
) {
    val state = rememberHistoryScreenState()
    state.scaffoldState.HistoryScreen(
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onDeleteAllClick = onDeleteAllClick,
        onSettingsClick = onSettingsClick,
        onBookClick = onBookClick,
        onBookInfoClick = onBookInfoClick,
        modifier = Modifier.testTag("HistoryScreenRoot"),
    )
    val resultConsumer = LocalNavigationResultConsumer.current
    val navigationResult: SerializedNavigationResult<ClearAllHistoryScreenResult>? by remember(
        resultConsumer,
    ) {
        resultConsumer.getResultState(Json, ClearAllHistoryScreenResultKey)
    }

    LaunchedEffect(navigationResult) {
        val result: SerializedNavigationResult<ClearAllHistoryScreenResult>? = navigationResult
        if (result != null) {
            // The received result is just a String, but getResult() will decode it to a Screen2Result instance.
            val screenResult = result.getResult()
            state.onNavResult(screenResult.confirmed)
            resultConsumer.clearResult(result.resultKey)
        }
    }
}
