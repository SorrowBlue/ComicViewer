package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavType
import com.sorrowblue.cmpdestinations.NavGraph
import com.sorrowblue.cmpdestinations.ScreenDestination
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal actual class SettingsDetailNavGraphImpl : NavGraph {
    actual override val startDestination: KClass<*> get() = throw RuntimeException("Stub!")
    actual override val route: KClass<*> get() = throw RuntimeException("Stub!")
    actual override val typeMap: Map<KType, NavType<*>> get() = throw RuntimeException("Stub!")
    actual override val screenDestinations: List<ScreenDestination> get() = throw RuntimeException("Stub!")
    actual override val nestedNavGraphs: List<NavGraph> get() = throw RuntimeException("Stub!")
    actual override val transitions: NavTransitions get() = throw RuntimeException("Stub!")
}
