package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenRoot
import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialFadeThrough
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@ContributesTo(AppScope::class)
interface AuthenticationNavigation {

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (AuthenticationNavKey::class as KClass<NavKey>) to (AuthenticationNavKey.serializer() as KSerializer<NavKey>),
        )
    }

    @Provides
    @IntoSet
    private fun provideAuthenticationEntry(
        factory: AuthenticationScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<AuthenticationNavKey>(metadata = NavDisplay.transitionMaterialFadeThrough()) {
            with(rememberRetained { factory.createAuthenticationScreenContext() }) {
                AuthenticationScreenRoot(
                    screenType = it.type,
                    onBackClick = navigator::goBack,
                    onComplete = navigator::goBack,
                )
            }
        }
    }
}

@Serializable
data class AuthenticationNavKey(val type: ScreenType) : ScreenKey
