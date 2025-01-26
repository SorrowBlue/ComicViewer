package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavType
import com.sorrowblue.comicviewer.framework.navigation.NavGraph
import com.sorrowblue.comicviewer.framework.navigation.ScreenDestination
import kotlin.reflect.KClass
import kotlin.reflect.KType

expect class ComicViewerAppNavGraphImpl() : NavGraph {
    override val startDestination: KClass<*>
    override val route: KClass<*>
    override val typeMap: Map<KType, NavType<*>>
    override val screenDestinations: List<ScreenDestination>
    override val nestedNavGraphs: List<NavGraph>
}
