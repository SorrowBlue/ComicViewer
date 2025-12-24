package com.sorrowblue.comicviewer.feature.collection.editor.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import com.sorrowblue.comicviewer.framework.ui.navigation.toPair
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer

@ContributesTo(AppScope::class)
interface CollectionEditProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(BasicCollectionCreateNavKey.serializer()),
            toPair(BasicCollectionEditNavKey.serializer()),
            toPair(SmartCollectionEditNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideBasicCollectionCreateNavEntry(
        factory: BasicCollectionCreateScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            basicCollectionCreateNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideBasicCollectionEditNavEntry(
        factory: BasicCollectionEditScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            basicCollectionEditNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideSmartCollectionCreateNavEntry(
        factory: SmartCollectionCreateScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            smartCollectionCreateNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideSmartCollectionEditNavEntry(
        factory: SmartCollectionEditScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            smartCollectionEditNavEntry(navigator)
        }
    }
}
