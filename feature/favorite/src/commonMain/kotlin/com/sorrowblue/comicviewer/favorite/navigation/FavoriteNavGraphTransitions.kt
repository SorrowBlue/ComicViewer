package com.sorrowblue.comicviewer.favorite.navigation

import com.sorrowblue.comicviewer.favorite.Favorite
import com.sorrowblue.comicviewer.favorite.FavoriteFolder
import com.sorrowblue.comicviewer.favorite.list.FavoriteList
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEdit
import com.sorrowblue.comicviewer.framework.ui.navigation.DestinationTransitions
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure

internal object FavoriteNavGraphTransitions : DestinationTransitions() {

    override val transitions = listOf(
        TransitionsConfigure(
            FavoriteList::class,
            Favorite::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            Favorite::class,
            FavoriteFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            FavoriteFolder::class,
            FavoriteFolder::class,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            Favorite::class,
            FavoriteEdit::class,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            FavoriteNavGraph::class,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}
