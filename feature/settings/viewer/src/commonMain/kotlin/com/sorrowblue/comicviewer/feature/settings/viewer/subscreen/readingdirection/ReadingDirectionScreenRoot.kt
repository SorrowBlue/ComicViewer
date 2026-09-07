/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection

@Composable
internal fun ReadingDirectionScreenRoot(
    bindingDirection: BindingDirection,
    onDismissRequest: () -> Unit,
) {
    val resultBus = LocalResultEventBus.current
    ReadingDirectionScreen(
        bindingDirection = bindingDirection,
        onBindingDirectionChange = dropUnlessResumed { direction ->
            resultBus.sendResult(direction)
            onDismissRequest()
        },
        onDismissRequest = dropUnlessResumed {
            onDismissRequest()
        },
    )
}
