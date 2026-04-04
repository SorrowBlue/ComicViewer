package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@Serializable
internal data object LicenseNavKey : NavKey

context(factory: LicenseScreenContext.Factory)
internal fun EntryProviderScope<NavKey>.pdfPluginNavEntry(navigator: Navigator) {
    entry<LicenseNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisX()) {
        with(rememberRetained { factory.createLicenseScreenContext() }) {
            LicenseScreenRoot(onBackClick = navigator::goBack)
        }
    }
}
