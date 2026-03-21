package com.sorrowblue.comicviewer.feature.collection.nav

import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import com.sorrowblue.comicviewer.framework.ui.navigation.asEntry
import com.sorrowblue.comicviewer.framework.ui.navigation3.NavKeyEntry
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
    private fun provideNavKeySubclassMap(): Set<NavKeyEntry> = setOf(
        SmartCollectionCreateNavKey.serializer().asEntry<SmartCollectionCreateNavKey>(),
    )
}

@Serializable
data class SmartCollectionCreateNavKey(
    val bookshelfId: BookshelfId? = null,
    val searchCondition: SearchCondition = SearchCondition(),
) : ScreenKey
