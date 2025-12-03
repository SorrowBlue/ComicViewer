package com.sorrowblue.comicviewer.framework.ui.navigation

import com.sorrowblue.comicviewer.framework.ui.navigation.TransitionsConfigure.Type
import kotlin.reflect.KClass

class TransitionConfigure(val form: String, val to: String, val type: TransitionsType)

enum class TransitionsType {
    SharedAxisX,
    SharedAxisY,
    SharedAxisZ,
    FadeThrough,
    ContainerTransform,
}

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

/*
abstract class DestinationTransitions {

    abstract val motionScheme: MotionScheme

    abstract val transitions: List<TransitionsConfigure>

    fun Any.hasRoute2(kClass: KClass<*>): Boolean {
        return this::class == kClass
    }

    fun AnimatedContentTransitionScope<Scene<NavKey>>.enterTransition(): EnterTransition {
        val initRoute = initialState.key
        val targetRoute = targetState.key
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> initRoute::class == configure.from && targetRoute::class == configure.to
                is GraphFrom -> if (configure.from != null) {
                    targetState.entries.any { it.contentKey::class == configure.to } &&
                        (initRoute::class == configure.from || initialState.entries.any { it.contentKey::class == configure.from) })
                } else {
                    targetState.entries.any { it.contentKey::class == configure.to }
                }
            }
        }?.let {
            when (it.type) {
                Type.SharedAxisX -> materialSharedAxisXIn(motionScheme)
                Type.SharedAxisY -> materialSharedAxisYIn(motionScheme)
                Type.SharedAxisZ -> materialSharedAxisZIn(motionScheme)
                Type.FadeThrough -> materialFadeThroughIn(motionScheme)
                Type.ContainerTransform -> materialContainerTransformIn()
            }
        } ?: EnterTransition.None
    }

    fun AnimatedContentTransitionScope<Scene<NavKey>>.exitTransition(): ExitTransition {
        val initRoute = initialState.key
        val targetRoute = targetState.key
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> initRoute.hasRoute2(configure.from) && targetRoute.hasRoute2(
                    configure.to
                )

                is GraphFrom -> if (configure.from != null) {
                    targetState.entries.any { it.hasRoute2(configure.to) } &&
                        (initRoute.hasRoute2(configure.from) || initialState.entries.any {
                            it.hasRoute2(configure.from)
                        })
                } else {
                    targetState.entries.any { it.hasRoute2(configure.to) }
                }
            }
        }?.let {
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
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> targetRoute.hasRoute2(configure.from) && initRoute.hasRoute2(
                    configure.to
                )

                is GraphFrom -> if (configure.from != null) {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
                        (targetRoute.hasRoute2(configure.from) || targetRoute.hierarchy.any {
                            it.hasRoute2(
                                configure.from
                            )
                        })
                } else {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) }
                }
            }
        }?.let {
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
        return transitions.firstOrNull { configure ->
            when (configure) {
                is BetweenScreen -> targetRoute.hasRoute2(configure.from) && initRoute.hasRoute2(
                    configure.to
                )

                is GraphFrom -> if (configure.from != null) {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) } &&
                        (targetRoute.hasRoute2(configure.from) || targetRoute.hierarchy.any {
                            it.hasRoute2(
                                configure.from
                            )
                        })
                } else {
                    initRoute.hierarchy.any { it.hasRoute2(configure.to) }
                }
            }
        }?.let {
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
*/
