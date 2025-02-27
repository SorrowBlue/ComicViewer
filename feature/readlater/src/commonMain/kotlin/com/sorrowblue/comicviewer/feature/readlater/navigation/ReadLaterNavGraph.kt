package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.readlater.ReadLater
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolder
import kotlinx.serialization.Serializable

@NavGraph(startDestination = ReadLater::class, transitions = ReadLaterNavGraphTransitions::class)
@Serializable
data object ReadLaterNavGraph {

    @DestinationInGraph<ReadLater>
    @DestinationInGraph<ReadLaterFolder>
    object Include
}
