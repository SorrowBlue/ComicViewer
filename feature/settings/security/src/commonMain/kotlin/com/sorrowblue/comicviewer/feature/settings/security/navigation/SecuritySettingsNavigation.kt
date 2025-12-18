package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationNavKey
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisX
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface SecuritySettingsNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (SecuritySettingsNavKey::class as KClass<NavKey>) to (SecuritySettingsNavKey.serializer() as KSerializer<NavKey>),
        )
    }

    @Provides
    @IntoSet
    private fun provideSecuritySettingsEntry(
        factory: SecuritySettingsScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<SecuritySettingsNavKey>(
            metadata = ListDetailSceneStrategy.detailPane("Settings")
                + NavDisplay.transitionMaterialSharedAxisX()
        ) {
            with(rememberRetained { factory.createSecuritySettingsScreenContext() }) {
                SecuritySettingsScreenRoot(
                    onBackClick = navigator::goBack,
                    onChangeAuthEnable = {
                        if (it) {
                            navigator.navigate(AuthenticationNavKey(ScreenType.Register))
                        } else {
                            navigator.navigate(AuthenticationNavKey(ScreenType.Erase))
                        }
                    },
                    onPasswordChangeClick = {
                        navigator.navigate(AuthenticationNavKey(ScreenType.Change))
                    },
                )
            }
        }
    }
}
