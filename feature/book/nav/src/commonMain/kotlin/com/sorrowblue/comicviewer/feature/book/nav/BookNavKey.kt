package com.sorrowblue.comicviewer.feature.book.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
data class BookNavKey(
    val bookshelfId: BookshelfId,
    val path: PathString,
    val name: String,
    val collectionId: CollectionId = CollectionId(),
) : ScreenKey

@ContributesTo(AppScope::class)
interface BookNav {

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (BookNavKey::class as KClass<NavKey>) to (BookNavKey.serializer() as KSerializer<NavKey>),
        )
    }
}
