package com.sorrowblue.comicviewer.feature.collection.navigation

import com.sorrowblue.comicviewer.feature.collection.Collection
import com.sorrowblue.comicviewer.feature.collection.CollectionFolder
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.CollectionEditorNavGraph
import com.sorrowblue.comicviewer.feature.collection.list.CollectionList
import com.sorrowblue.comicviewer.framework.ui.navigation.BetweenScreen
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.GraphFrom
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

object CollectionNavGraphTransitions : DestinationTransitions() {
    override val transitions = listOf(
        BetweenScreen(
            CollectionList::class,
            Collection::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        BetweenScreen(
            Collection::class,
            CollectionFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        BetweenScreen(
            CollectionFolder::class,
            CollectionFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        GraphFrom(
            CollectionList::class,
            CollectionEditorNavGraph::class,
            TransitionsConfigure.Type.SharedAxisZ
        ),
        GraphFrom(
            null,
            CollectionNavGraph::class,
            TransitionsConfigure.Type.ContainerTransform
        ),
    )
}
