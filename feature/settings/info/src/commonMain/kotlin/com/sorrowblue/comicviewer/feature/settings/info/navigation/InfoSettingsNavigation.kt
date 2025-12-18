package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.settings.info.AppInfoSettingsScreenRoot
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface InfoSettingsNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(InfoSettingsNavKey.serializer()),
            toPair(LicenseNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideInfoSettingsEntry(): EntryProviderScope<NavKey>.(Navigator) -> Unit =
        { navigator ->
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
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<LicenseNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisX()) {
            with(rememberRetained { factory.createLicenseScreenContext() }) {
                LicenseScreenRoot(onBackClick = navigator::goBack)
            }
        }
    }
}
