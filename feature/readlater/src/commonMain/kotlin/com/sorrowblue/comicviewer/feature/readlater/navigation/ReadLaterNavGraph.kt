package com.sorrowblue.comicviewer.feature.readlater.navigation

import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.cmpdestinations.annotation.NavGraph
import com.sorrowblue.comicviewer.feature.readlater.ReadLater
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolder
import com.sorrowblue.comicviewer.framework.ui.navigation.TabDisplayRoute
import kotlin.reflect.KClass
import kotlinx.serialization.Serializable
import jakarta.inject.Singleton

@NavGraph(
    startDestination = ReadLater::class,
    destinations = [ReadLater::class, ReadLaterFolder::class],
    transitions = NavTransitions.ApplyParent::class
)
@Serializable
data object ReadLaterNavGraph

@Singleton
internal class ReadLaterNavGraphTabDisplayRoute : TabDisplayRoute {
    override val routes: List<KClass<*>> =
        listOf(ReadLater::class, ReadLaterFolder::class)
}
