package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenContext
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
interface BookshelfInfoProviders {
    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> =
        listOf(
            toPair(BookshelfInfoNavKey.serializer()),
            toPair(BookshelfDeleteNavKey.serializer()),
            toPair(BookshelfNotificationNavKey.serializer()),
        )

    @Provides
    @IntoSet
    private fun provideBookshelfInfoNavEntry(
        factory: BookshelfInfoScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            bookshelfInfoNavEntry(navigator, "Bookshelf")
        }
    }

    @Provides
    @IntoSet
    private fun provideBookshelfNotificationNavEntry(): EntryProviderScope<NavKey>.(
        Navigator,
    ) -> Unit =
        { navigator -> bookshelfNotificationNavEntry(navigator) }

    @Provides
    @IntoSet
    private fun provideBookshelfDeleteNavEntry(
        factory: BookshelfDeleteScreenContext.Factory,
    ): EntryProviderScope<NavKey>.(Navigator) -> Unit = { navigator ->
        with(factory) {
            bookshelfDeleteNavEntry(navigator)
        }
    }
}
