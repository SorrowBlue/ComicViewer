package com.sorrowblue.comicviewer.feature.collection.navigation

import com.sorrowblue.comicviewer.feature.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.CollectionFolder
import com.sorrowblue.comicviewer.feature.collection.list.CollectionList
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object CollectionNavGraphTransitions : DestinationTransitions() {
    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            CollectionList::class,
            Collection::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            Collection::class,
            CollectionFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            CollectionNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
