package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.sorrowblue.comicviewer.framework.navigation.NavTransition
import com.sorrowblue.comicviewer.framework.ui.animation.materialContainerTransformIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialContainerTransformOut
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialFadeThroughOut
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisXIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisXOut
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisYIn
import com.sorrowblue.comicviewer.framework.ui.animation.materialSharedAxisYOut
import kotlin.reflect.KClass
import logcat.logcat

data class TransitionsConfigure(
    val enterRoute: KClass<*>,
    val exitRoute: KClass<*>?,
    val type: Type,
) {

    enum class Type {
        SharedAxisX,
        SharedAxisY,
        FadeThrough,
        ContainerTransform,
    }
}

abstract class DestinationTransitions : NavTransition() {

    companion object {
        var slideDistance = -1
    }

    abstract val transitions: List<TransitionsConfigure>

    private fun NavDestination.hasRoute2(clazz: KClass<*>?): Boolean {
        return clazz?.let { hasRoute(it) } == true
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(): EnterTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        val transition = transitions.firstOrNull { conf ->
            (conf.exitRoute == null && targetRoute.hierarchy.any { it.hasRoute2(conf.enterRoute) })
                || (initRoute.hasRoute2(conf.enterRoute) && targetRoute.hasRoute2(conf.exitRoute))
        }
        return transition?.let {
            when (transition.type) {
                TransitionsConfigure.Type.SharedAxisX ->
                    materialSharedAxisXIn(true, slideDistance)

                TransitionsConfigure.Type.SharedAxisY ->
                    materialSharedAxisYIn(true, slideDistance)

                TransitionsConfigure.Type.FadeThrough -> materialFadeThroughIn()
                TransitionsConfigure.Type.ContainerTransform -> materialContainerTransformIn()
            }
        } ?: EnterTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(): ExitTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        val transition = transitions.firstOrNull { conf ->
            (conf.exitRoute == null && initRoute.hierarchy.any { it.hasRoute2(conf.enterRoute) }) ||
                (initRoute.hasRoute2(conf.enterRoute) && targetRoute.hasRoute2(conf.exitRoute))
        }
        return transition?.let {
            when (transition.type) {
                TransitionsConfigure.Type.SharedAxisX -> materialSharedAxisXOut(true, slideDistance)
                TransitionsConfigure.Type.SharedAxisY -> materialSharedAxisYOut(true, slideDistance)
                TransitionsConfigure.Type.FadeThrough -> materialFadeThroughOut()
                TransitionsConfigure.Type.ContainerTransform -> materialContainerTransformOut()
            }
        } ?: ExitTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(): EnterTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        val transition = transitions.firstOrNull { conf ->
            (conf.exitRoute == null && targetRoute.hierarchy.any { it.hasRoute2(conf.enterRoute) }) ||
                (initRoute.hasRoute2(conf.exitRoute) && targetRoute.hasRoute2(conf.enterRoute))
        }
        return transition?.let {
            when (transition.type) {
                TransitionsConfigure.Type.SharedAxisX -> materialSharedAxisXIn(false, slideDistance)
                TransitionsConfigure.Type.SharedAxisY -> materialSharedAxisYIn(true, slideDistance)
                TransitionsConfigure.Type.FadeThrough -> materialFadeThroughIn()
                TransitionsConfigure.Type.ContainerTransform -> materialContainerTransformIn()
            }
        } ?: EnterTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(): ExitTransition {
        val initRoute = initialState.destination
        val targetRoute = targetState.destination
        return transitions.firstOrNull { conf ->
            (conf.exitRoute == null && initRoute.hierarchy.any { it.hasRoute2(conf.enterRoute) }) ||
                (initRoute.hasRoute2(conf.exitRoute) && targetRoute.hasRoute2(conf.enterRoute))
        }?.let {
            when (it.type) {
                TransitionsConfigure.Type.SharedAxisX ->
                    materialSharedAxisXOut(false, slideDistance)

                TransitionsConfigure.Type.SharedAxisY ->
                    materialSharedAxisYOut(true, slideDistance)

                TransitionsConfigure.Type.FadeThrough -> materialFadeThroughOut()
                TransitionsConfigure.Type.ContainerTransform -> materialContainerTransformOut()
            }
        } ?: ExitTransition.None
    }

    override fun AnimatedContentTransitionScope<NavBackStackEntry>.sizeTransform(): SizeTransform? {
        return null
    }
}
