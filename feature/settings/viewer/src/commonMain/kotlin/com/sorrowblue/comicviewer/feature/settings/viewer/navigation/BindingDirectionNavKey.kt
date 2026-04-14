package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.settings.viewer.BindingDirectionScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.Serializable

@Serializable
internal data class BindingDirectionNavKey(val bindingDirection: BindingDirection) : NavKey

internal val BindingDirectionScreenResultKey = SerializableNavigationResultKey(
    serializer = BindingDirection.serializer(),
    resultKey = "BindingDirectionScreenResultKey",
)

internal fun EntryProviderScope<NavKey>.bindingDirectionNavEntry(navigator: Navigator) {
    entry<BindingDirectionNavKey>(
        metadata = metadata {
            put(DialogSceneStrategy.Companion.DialogKey, DialogProperties())
        },
    ) { navKey ->
        val resultProducer = LocalNavigationResultProducer.current
        BindingDirectionScreen(
            bindingDirection = navKey.bindingDirection,
            onClick = {
                resultProducer.setResult(BindingDirectionScreenResultKey, it)
                navigator.goBack()
            },
        )
    }
}
