package com.sorrowblue.comicviewer.framework.navigation

import androidx.navigation.NavType
import kotlin.reflect.KClass
import kotlin.reflect.KType

interface NavGraph {
    val startDestination: KClass<*>
    val route: KClass<*>
    val typeMap: Map<KType, NavType<*>>
    val screenDestinations: List<ScreenDestination>
    val nestedNavGraphs: List<NavGraph>

    val transition: NavTransition
}
