package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.cmpdestinations.annotation.DestinationInGraph
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.readlater.ReadLater
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Singleton

@NavGraph(startDestination = ReadLater::class, transitions = ReadLaterNavGraphTransitions::class)
@Serializable
data object ReadLaterNavGraph {

    @DestinationInGraph<ReadLater>
    @DestinationInGraph<ReadLaterFolder>
    object Include
}

@Singleton
internal class ReadLaterNavGraphTabDisplayRoute : TabDisplayRoute {
    override val routes: List<KClass<*>> =
        listOf(ReadLater::class, ReadLaterFolder::class)
}
