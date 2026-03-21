package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenContext
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.ScreenEntryProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import io.github.takahirom.rin.rememberRetained
import kotlinx.serialization.Serializable

@ContributesTo(AppScope::class)
interface ReceiveBookNavigation {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> =
        setOf(ReceiveBookNavKey.serializer().asEntry())

    @Provides
    @IntoSet
    private fun provideReceiveBookEntry(
        factory: ReceiveBookScreenContext.Factory,
    ): ScreenEntryProvider = { navigator ->
        entry<ReceiveBookNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisZ()) { it ->
            with(rememberRetained { factory.createReceiveBookScreenContext() }) {
                ReceiveBookScreenRoot(
                    uri = it.uri,
                    onBackClick = navigator::goBack,
                )
            }
        }
    }
}

@Serializable
data class ReceiveBookNavKey(val uri: String) : ScreenKey
