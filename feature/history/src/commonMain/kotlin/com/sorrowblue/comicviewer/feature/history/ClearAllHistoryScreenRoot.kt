package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal val ClearAllHistoryScreenResultKey = SerializableNavigationResultKey(
    serializer = ClearAllHistoryScreenResult.serializer(),
    resultKey = "ClearAllHistoryScreenResultKey",
)

@Serializable
internal data class ClearAllHistoryScreenResult(val confirmed: Boolean)

@Composable
internal fun ClearAllHistoryScreenRoot(onClose: () -> Unit) {
    val resultProducer = LocalNavigationResultProducer.current
    ClearAllHistoryScreen(
        onDismissRequest = {
            resultProducer.setResult(
                Json,
                ClearAllHistoryScreenResultKey,
                ClearAllHistoryScreenResult(confirmed = false),
            )
            onClose()
        },
        onConfirm = {
            resultProducer.setResult(
                Json,
                ClearAllHistoryScreenResultKey,
                ClearAllHistoryScreenResult(confirmed = true),
            )
            onClose()
        },
        modifier = Modifier.testTag("ClearAllHistoryScreenRoot"),
    )
}
