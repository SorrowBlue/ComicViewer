package com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
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
interface BookshelfEditProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(BookshelfEditNavKey.serializer()),
            toPair(BookshelfSelectionNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideBookshelfEditNavEntry(
        factory: BookshelfEditScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            bookshelfEditNavEntry(navigator)
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfSelectionNavEntry(): EntryProviderScope<NavKey>.(
        Navigator,
    ) -> Unit =
        { navigator -> bookshelfSelectionNavEntry(navigator) }

    @Provides
    @IntoSet
    private fun provideBookshelfDiscardConfirmNavEntry(): EntryProviderScope<NavKey>.(
        Navigator,
    ) -> Unit =
        { navigator -> bookshelfDiscardConfirmNavEntry(navigator) }
}
