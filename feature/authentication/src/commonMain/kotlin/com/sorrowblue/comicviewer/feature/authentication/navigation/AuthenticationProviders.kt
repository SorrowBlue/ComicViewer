package com.sorrowblue.comicviewer.feature.authentication.navigation

import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AuthenticationProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(AuthenticationNavKey.serializer().asEntry())

    @Provides
    @IntoSet
    private fun provideAuthenticationNavEntry(
        factory: AuthenticationScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        with(factory) {
            authenticationNavEntry(navigator)
        }
    }
}
