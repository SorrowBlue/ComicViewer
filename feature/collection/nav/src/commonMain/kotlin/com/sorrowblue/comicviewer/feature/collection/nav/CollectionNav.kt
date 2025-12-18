package com.sorrowblue.comicviewer.feature.collection.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ElementsIntoSet
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@ContributesTo(AppScope::class)
interface CollectionNav {

    @Provides
    @ElementsIntoSet
    private fun provideNavKeySubclassMap(): List<Pair<KClass<NavKey>, KSerializer<NavKey>>> {
        return listOf(
            (BasicCollectionAddNavKey::class as KClass<NavKey>) to (BasicCollectionAddNavKey.serializer() as KSerializer<NavKey>),
            (SmartCollectionCreateNavKey::class as KClass<NavKey>) to (SmartCollectionCreateNavKey.serializer() as KSerializer<NavKey>),
        )
    }

}

@Serializable
data class BasicCollectionAddNavKey(
    val bookshelfId: BookshelfId,
    val path: PathString,
) : ScreenKey

@Serializable
data class SmartCollectionCreateNavKey(
    val bookshelfId: BookshelfId? = null,
    val searchCondition: SearchCondition = SearchCondition(),
) : ScreenKey
