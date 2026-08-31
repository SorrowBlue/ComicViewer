/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation3.runtime.result.LocalResultEventBus
import kotlinx.serialization.Serializable

@Serializable
internal data class ClearAllHistoryScreenResult(val confirmed: Boolean)

@Composable
internal fun ClearAllHistoryScreenRoot(onClose: () -> Unit) {
    val resultBus = LocalResultEventBus.current

    ClearAllHistoryScreen(
        onDismissRequest = {
            resultBus.sendResult(ClearAllHistoryScreenResult(confirmed = false))
            onClose()
        },
        onConfirm = {
            resultBus.sendResult(ClearAllHistoryScreenResult(confirmed = true))
            onClose()
        },
        modifier = Modifier.testTag("ClearAllHistoryScreenRoot"),
    )
}
