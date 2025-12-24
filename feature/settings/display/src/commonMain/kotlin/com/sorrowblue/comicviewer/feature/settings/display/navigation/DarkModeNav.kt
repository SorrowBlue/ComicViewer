package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenRoot
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
internal data object DarkModeNavKey : ScreenKey

context(factory: DarkModeScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.darkModeNavEntry(navigator: Navigator) {
    entry<DarkModeNavKey>(metadata = DialogSceneStrategy.dialog()) {
        with(rememberRetained { factory.createDarkModeScreenContext() }) {
            DarkModeScreenRoot(
                onDismissRequest = navigator::goBack,
                onComplete = navigator::goBack,
            )
        }
    }
}
