package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

val ClearAllHistoryScreenResultKey = SerializableNavigationResultKey(
    serializer = ClearAllHistoryScreenResult.serializer(),
    resultKey = "ClearAllHistoryScreenResultKey",
)

@Serializable
data class ClearAllHistoryScreenResult(val confirmed: Boolean)

@Composable
fun ClearAllHistoryScreenRoot(onClose: () -> Unit) {
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
    )
}
