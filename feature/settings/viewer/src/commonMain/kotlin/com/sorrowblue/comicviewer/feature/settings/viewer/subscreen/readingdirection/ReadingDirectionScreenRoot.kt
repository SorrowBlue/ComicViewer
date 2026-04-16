package com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.dropUnlessResumed
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult

@Composable
internal fun ReadingDirectionScreenRoot(
    bindingDirection: BindingDirection,
    onDismissRequest: () -> Unit,
) {
    val resultProducer = LocalNavigationResultProducer.current
    ReadingDirectionScreen(
        bindingDirection = bindingDirection,
        onBindingDirectionChange = dropUnlessResumed { direction ->
            resultProducer.setResult(BindingDirectionScreenResultKey, direction)
            onDismissRequest()
        },
        onDismissRequest = dropUnlessResumed {
            onDismissRequest()
        }
    )
}

internal val BindingDirectionScreenResultKey = SerializableNavigationResultKey(
    serializer = BindingDirection.serializer(),
    resultKey = "BindingDirectionScreenResultKey",
)
