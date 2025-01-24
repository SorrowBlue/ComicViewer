package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavType
import com.sorrowblue.comicviewer.framework.navigation.NavGraph
import com.sorrowblue.comicviewer.framework.navigation.ScreenDestination
import kotlin.reflect.KClass
import kotlin.reflect.KType

actual class ComicViewerAppNavGraphImpl actual constructor() : NavGraph {
    override val startDestination: KClass<*>
        get() = TODO("Not yet implemented")
    override val route: KClass<*>
        get() = TODO("Not yet implemented")
    override val typeMap: Map<KType, NavType<*>>
        get() = TODO("Not yet implemented")
    override val screenDestinations: List<ScreenDestination>
        get() = TODO("Not yet implemented")
    override val nestedNavGraphs: List<NavGraph>
        get() = TODO("Not yet implemented")
}
