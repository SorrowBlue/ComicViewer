package com.sorrowblue.comicviewer.feature.collection.navigation

import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.CollectionFolder
import com.sorrowblue.comicviewer.feature.collection.add.navigation.BasicCollectionAddNavGraph
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollection
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.CollectionEditorNavGraph
import com.sorrowblue.comicviewer.feature.collection.list.CollectionList
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

@NavGraph(
    startDestination = CollectionList::class,
    transitions = CollectionNavGraphTransitions::class,
    destinations = [
        Collection::class,
        CollectionList::class,
        CollectionFolder::class,
        DeleteCollection::class,
    ],
    nestedGraphs = [
        BasicCollectionAddNavGraph::class,
        CollectionEditorNavGraph::class,
    ],
)
@Serializable
data object CollectionNavGraph

@Singleton
internal class CollectionNavGraphTabDisplayRoute : TabDisplayRoute {
    override val routes: List<KClass<*>> =
        listOf(Collection::class, CollectionList::class, CollectionFolder::class)
}
