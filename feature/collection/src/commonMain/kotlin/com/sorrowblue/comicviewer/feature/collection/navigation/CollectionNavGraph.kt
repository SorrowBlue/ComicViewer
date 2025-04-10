package com.sorrowblue.comicviewer.feature.collection.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.cmpdestinations.annotation.NestedNavGraph
import com.sorrowblue.comicviewer.feature.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.CollectionFolder
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAdd
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.CollectionEditorNavGraph
import com.sorrowblue.comicviewer.feature.collection.list.CollectionList
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

@NavGraph(
    startDestination = CollectionList::class,
    transitions = CollectionNavGraphTransitions::class
)
@Serializable
data object CollectionNavGraph {
    @DestinationInGraph<CollectionList>
    @DestinationInGraph<Collection>
    @DestinationInGraph<CollectionFolder>
    @DestinationInGraph<BasicCollectionAdd>
    @NestedNavGraph<CollectionEditorNavGraph>
    object Include
}

@Singleton
internal class CollectionNavGraphTabDisplayRoute : TabDisplayRoute {
    override val routes: List<KClass<*>> =
        listOf(Collection::class, CollectionList::class, CollectionFolder::class)

}
