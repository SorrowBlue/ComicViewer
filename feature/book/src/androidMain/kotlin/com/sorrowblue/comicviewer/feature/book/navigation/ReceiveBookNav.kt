package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenContext
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
data class ReceiveBookNavKey(val uri: String) : NavKey

context(factory: ReceiveBookScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.receiveBookNavEntry(navigator: Navigator) {
    entry<ReceiveBookNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisZ()) {
        with(rememberRetained { factory.createReceiveBookScreenContext() }) {
            ReceiveBookScreenRoot(
                uri = it.uri,
                onBackClick = navigator::goBack,
            )
        }
    }
}
