package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained

@ContributesTo(AppScope::class)
interface InfoSettingsNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        InfoSettingsNavKey.serializer().asEntry(),
        LicenseNavKey.serializer().asEntry(),
    )

    @Provides
    @IntoSet
    private fun provideInfoSettingsEntry(): ScreenEntryProvider = { navigator ->
        entry<InfoSettingsNavKey>(
            metadata = ListDetailSceneStrategy.detailPane("Settings") +
                    NavDisplay.transitionMaterialSharedAxisX(),
        ) {
            AppInfoSettingsScreenRoot(
                onBackClick = navigator::goBack,
                onLicenceClick = { navigator.navigate(LicenseNavKey) },
            )
        }
    }

    @Provides
    @IntoSet
    private fun provideLicenseEntry(
        factory: LicenseScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        entry<LicenseNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisX()) {
            with(rememberRetained { factory.createLicenseScreenContext() }) {
                LicenseScreenRoot(onBackClick = navigator::goBack)
            }
        }
    }
}
