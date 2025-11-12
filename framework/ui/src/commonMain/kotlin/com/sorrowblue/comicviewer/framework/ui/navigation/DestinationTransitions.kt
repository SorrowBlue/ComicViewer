package com.sorrowblue.comicviewer.framework.ui.navigation

import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure.Type
import kotlin.reflect.KClass

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

data class GraphFrom(val from: KClass<*>?, val to: KClass<*>, override val type: Type) :
    TransitionsConfigure {
    override fun toString(): String =
        "GraphFrom(from=${from?.simpleName}, to=${to.simpleName}, type=$type)"
}

data class BetweenScreen(val from: KClass<*>, val to: KClass<*>, override val type: Type) :
    TransitionsConfigure {
    override fun toString(): String =
        "BetweenScreen(from=${from.simpleName}, to=${to.simpleName}, type=$type)"
}

// abstract class DestinationTransitions : NavTransitions() {
//
//    abstract val transitions: List<TransitionsConfigure>
//
//    private fun NavDestination.hasRoute2(clazz: KClass<*>?): Boolean {
//        return clazz?.let { hasRoute(it) } == true
//    }
//
//    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
//        val initRoute = initialState.destination
//        val targetRoute = targetState.destination
//        return transitions.firstOrNull { configure ->
//            when (configure) {
//                is BetweenScreen -> initRoute.hasRoute2(configure.from) && targetRoute.hasRoute2(configure.to)
//                is GraphFrom -> if (configure.from != null) {
//                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
//                        (initRoute.hasRoute2(configure.from) || initRoute.hierarchy.any { it.hasRoute2(configure.from) })
//                } else {
//                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) }
//                }
//            }
//        }?.let {
//            when (it.type) {
//                Type.SharedAxisX -> materialSharedAxisXIn()
//                Type.SharedAxisY -> materialSharedAxisYIn()
//                Type.SharedAxisZ -> materialSharedAxisZIn()
//                Type.FadeThrough -> materialFadeThroughIn()
//                Type.ContainerTransform -> materialContainerTransformIn()
//            }
//        } ?: EnterTransition.None
//    }
//
//    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
//        val initRoute = initialState.destination
//        val targetRoute = targetState.destination
//        return transitions.firstOrNull { configure ->
//            when (configure) {
//                is BetweenScreen -> initRoute.hasRoute2(configure.from) && targetRoute.hasRoute2(configure.to)
//                is GraphFrom -> if (configure.from != null) {
//                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
//                        (initRoute.hasRoute2(configure.from) || initRoute.hierarchy.any { it.hasRoute2(configure.from) })
//                } else {
//                    targetRoute.hierarchy.any { it.hasRoute2(configure.to) }
//                }
//            }
//        }?.let {
//            when (it.type) {
//                Type.SharedAxisX -> materialSharedAxisXOut()
//                Type.SharedAxisY -> materialSharedAxisYOut()
//                Type.SharedAxisZ -> materialSharedAxisZOut()
//                Type.FadeThrough -> materialFadeThroughOut()
//                Type.ContainerTransform -> materialContainerTransformOut()
//            }
//        } ?: ExitTransition.None
//    }
//
//    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
//        val initRoute = initialState.destination
//        val targetRoute = targetState.destination
//        return transitions.firstOrNull { configure ->
//            when (configure) {
//                is BetweenScreen -> targetRoute.hasRoute2(configure.from) && initRoute.hasRoute2(configure.to)
//                is GraphFrom -> if (configure.from != null) {
//                    initRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
//                        (targetRoute.hasRoute2(configure.from) || targetRoute.hierarchy.any { it.hasRoute2(configure.from) })
//                } else {
//                    initRoute.hierarchy.any { it.hasRoute2(configure.to) }
//                }
//            }
//        }?.let {
//            when (it.type) {
//                Type.SharedAxisX -> materialSharedAxisXIn()
//                Type.SharedAxisY -> materialSharedAxisYIn()
//                Type.SharedAxisZ -> materialSharedAxisZIn()
//                Type.FadeThrough -> materialFadeThroughIn()
//                Type.ContainerTransform -> materialContainerTransformIn()
//            }
//        } ?: EnterTransition.None
//    }
//
//    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
//        val initRoute = initialState.destination
//        val targetRoute = targetState.destination
//        return transitions.firstOrNull { configure ->
//            when (configure) {
//                is BetweenScreen -> targetRoute.hasRoute2(configure.from) && initRoute.hasRoute2(configure.to)
//                is GraphFrom -> if (configure.from != null) {
//                    initRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
//                        (targetRoute.hasRoute2(configure.from) || targetRoute.hierarchy.any { it.hasRoute2(configure.from) })
//                } else {
//                    initRoute.hierarchy.any { it.hasRoute2(configure.to) }
//                }
//            }
//        }?.let {
//            when (it.type) {
//                Type.SharedAxisX -> materialSharedAxisXOut()
//                Type.SharedAxisY -> materialSharedAxisYOut()
//                Type.SharedAxisZ -> materialSharedAxisZOut()
//                Type.FadeThrough -> materialFadeThroughOut()
//                Type.ContainerTransform -> materialContainerTransformOut()
//            }
//        } ?: ExitTransition.None
//    }
//
//    override fun AnimatedContentTransitionScope<NavBackStackEntry>.sizeTransform(): SizeTransform? {
//        return null
//    }
// }
