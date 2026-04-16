package com.sorrowblue.comicviewer.feature.settings.viewer.navigation

import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.domain.model.settings.BindingDirection
import com.sorrowblue.comicviewer.feature.settings.viewer.subscreen.readingdirection.ReadingDirectionScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.SerializableNavigationResultKey
import kotlinx.serialization.Serializable

@Serializable
internal data class BindingDirectionNavKey(val bindingDirection: BindingDirection) : NavKey

internal fun EntryProviderScope<NavKey>.bindingDirectionNavEntry(navigator: Navigator) {
    entry<BindingDirectionNavKey>(
        metadata = metadata {
            put(DialogSceneStrategy.Companion.DialogKey, DialogProperties())
        },
    ) { navKey ->
        ReadingDirectionScreenRoot(
            bindingDirection = navKey.bindingDirection,
            onDismissRequest = dropUnlessResumed { navigator.goBack() },
        )
    }
}
