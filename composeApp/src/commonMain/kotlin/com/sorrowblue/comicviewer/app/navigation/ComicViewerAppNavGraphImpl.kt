package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.NavGraph
import com.sorrowblue.cmpdestinations.ScreenDestination
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class ComicViewerAppNavGraphImpl() : NavGraph {
    override val startDestination: KClass<*>
    override val route: KClass<*>
    override val typeMap: Map<KType, NavType<*>>
    override val screenDestinations: List<ScreenDestination>
    override val nestedNavGraphs: List<NavGraph>
    override val transitions: NavTransitions
}
