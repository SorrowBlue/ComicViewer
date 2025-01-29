package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.comicviewer.feature.readlater.ReadLater
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolder
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable

@NavGraph(startDestination = ReadLater::class, transition = ReadLaterNavGraphTransitions::class)
@Serializable
data object ReadLaterNavGraph {

    @DestinationInGraph<ReadLater>
    @DestinationInGraph<ReadLaterFolder>
    object Include
}
