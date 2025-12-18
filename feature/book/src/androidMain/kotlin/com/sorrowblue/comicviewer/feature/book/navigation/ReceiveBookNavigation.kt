package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenContext
import com.sorrowblue.comicviewer.feature.book.receive.ReceiveBookScreenRoot
import com.sorrowblue.comicviewer.framework.ui.animation.transitionMaterialSharedAxisZ
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
interface ReceiveBookNavigation {

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (ReceiveBookNavKey::class as KClass<NavKey>) to (ReceiveBookNavKey.serializer() as KSerializer<NavKey>),
        )
    }

    @Provides
    @IntoSet
    private fun provideReceiveBookEntry(
        factory: ReceiveBookScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        entry<ReceiveBookNavKey>(metadata = NavDisplay.transitionMaterialSharedAxisZ()) {
            with(rememberRetained { factory.createReceiveBookScreenContext() }) {
                ReceiveBookScreenRoot(
                    uri = it.uri,
                    onBackClick = navigator::goBack
                )
            }
        }
    }
}

@Serializable
data class ReceiveBookNavKey(val uri: String) : ScreenKey
