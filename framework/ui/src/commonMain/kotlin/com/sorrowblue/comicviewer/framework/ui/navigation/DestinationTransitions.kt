package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sorrowblue.cmpdestinations.animation.NavTransitions
import com.sorrowblue.comicviewer.framework.ui.animation.materialContainerTransformIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialContainerTransformOut
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisXIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisXOut
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisYIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisYOut
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisZOut
import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure.Type
import kotlin.reflect.KClass
import logcat.logcat

sealed interface TransitionsConfigure {
    val type: Type

    enum class Type {
        SharedAxisX,
        SharedAxisY,
        SharedAxisZ,
        FadeThrough,
        ContainerTransform,
    }
}

data class GraphFrom(
    val from: KClass<*>?,
    val to: KClass<*>,
    override val type: Type,
) : TransitionsConfigure {

    override fun toString(): String {
        return "GraphFrom(from=${from?.simpleName}, to=${to.simpleName}, type=$type)"
    }
}


data class BetweenScreen(
    val from: KClass<*>,
    val to: KClass<*>,
    override val type: Type,
) : TransitionsConfigure {

    override fun toString(): String {
        return "BetweenScreen(from=${from.simpleName}, to=${to.simpleName}, type=$type)"
    }
}

abstract class DestinationTransitions : NavTransitions() {

    abstract val transitions: List<TransitionsConfigure>

    private fun NavDestination.hasRoute2(clazz: KClass<*>?): Boolean {
        return clazz?.let { hasRoute(it) } == true
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        logcat { "***route enterTransition ${initRoute.route} ${targetRoute.route}" }
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> initRoute.hasRoute2(configure.from) && targetRoute.hasRoute2(configure.to)
                is GraphFrom -> if (configure.from != null) {
                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
                        (initRoute.hasRoute2(configure.from) || initRoute.hierarchy.any { it.hasRoute2(configure.from) })
                } else {
                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) }
                }
            }
        }?.let {
            logcat { "***** enterTransition $it" }
            when (it.type) {
                Type.SharedAxisX -> materialSharedAxisXIn()
                Type.SharedAxisY -> materialSharedAxisYIn()
                Type.SharedAxisZ -> materialSharedAxisZIn()
                Type.FadeThrough -> materialFadeThroughIn()
                Type.ContainerTransform -> materialContainerTransformIn()
            }
        } ?: EnterTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        logcat { "***route exitTransition ${initRoute.route} ${targetRoute.route}" }
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> initRoute.hasRoute2(configure.from) && targetRoute.hasRoute2(configure.to)
                is GraphFrom -> if (configure.from != null) {
                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
                        (initRoute.hasRoute2(configure.from) || initRoute.hierarchy.any { it.hasRoute2(configure.from) })
                } else {
                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) }
                }
            }
        }?.let {
            logcat { "***** exitTransition $it" }
            when (it.type) {
                Type.SharedAxisX -> materialSharedAxisXOut()
                Type.SharedAxisY -> materialSharedAxisYOut()
                Type.SharedAxisZ -> materialSharedAxisZOut()
                Type.FadeThrough -> materialFadeThroughOut()
                Type.ContainerTransform -> materialContainerTransformOut()
            }
        } ?: ExitTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        logcat { "***route popEnterTransition ${initRoute.route} ${targetRoute.route}" }
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> targetRoute.hasRoute2(configure.from) && initRoute.hasRoute2(configure.to)
                is GraphFrom -> if (configure.from != null) {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
                        (targetRoute.hasRoute2(configure.from) || targetRoute.hierarchy.any { it.hasRoute2(configure.from) })
                } else {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) }
                }
            }
        }?.let {
            logcat { "***** popEnterTransition $it" }
            when (it.type) {
                Type.SharedAxisX -> materialSharedAxisXIn()
                Type.SharedAxisY -> materialSharedAxisYIn()
                Type.SharedAxisZ -> materialSharedAxisZIn()
                Type.FadeThrough -> materialFadeThroughIn()
                Type.ContainerTransform -> materialContainerTransformIn()
            }
        } ?: EnterTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        logcat { "***route popExitTransition ${initRoute.route} ${targetRoute.route}" }
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> targetRoute.hasRoute2(configure.from) && initRoute.hasRoute2(configure.to)
                is GraphFrom -> if (configure.from != null) {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
                        (targetRoute.hasRoute2(configure.from) || targetRoute.hierarchy.any { it.hasRoute2(configure.from) })
                } else {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) }
                }
            }
        }?.let {
            logcat { "***** popExitTransition $it" }
            when (it.type) {
                Type.SharedAxisX -> materialSharedAxisXOut()
                Type.SharedAxisY -> materialSharedAxisYOut()
                Type.SharedAxisZ -> materialSharedAxisZOut()
                Type.FadeThrough -> materialFadeThroughOut()
                Type.ContainerTransform -> materialContainerTransformOut()
            }
        } ?: ExitTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.sizeTransform(): SizeTransform? {
        return null
    }
}
