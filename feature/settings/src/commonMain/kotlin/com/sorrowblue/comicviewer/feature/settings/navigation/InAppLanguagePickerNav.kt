package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
internal data object InAppLanguagePickerNavKey : ScreenKey

context(factory: InAppLanguagePickerScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.inAppLanguagePickerNavEntry(navigator: Navigator) {
    entry<InAppLanguagePickerNavKey>(
        metadata = ListDetailSceneStrategy.detailPane("Settings") +
            NavDisplay.transitionMaterialSharedAxisX(),
    ) {
        with(rememberRetained { factory.createInAppLanguagePickerScreenContext() }) {
            InAppLanguagePickerScreenRoot(onBackClick = navigator::goBack)
        }
    }
}
