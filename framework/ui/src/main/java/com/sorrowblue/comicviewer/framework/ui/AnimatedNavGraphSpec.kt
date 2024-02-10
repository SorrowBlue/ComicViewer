package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.spec.NavGraphSpec

interface AnimatedNavGraphSpec : NavGraphSpec {

    val transitions: List<TransitionsConfigure> get() = emptyList()

    val showNavigation: List<String> get() = emptyList()

    fun animations(slideDistance: Int) = NestedNavGraphDefaultAnimations(
        enterTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.enterRoute && targetRoute == it.exitRoute)
            }
            transition?.let {
                when (transition.type) {
                    TransitionsConfigure.Type.SharedAxisX -> materialSharedAxisXIn(
                        true,
                        slideDistance
                    )

                    TransitionsConfigure.Type.SharedAxisY -> materialSharedAxisYIn(
                        true,
                        slideDistance
                    )

                    TransitionsConfigure.Type.FadeThrough -> materialFadeThroughIn()
                    TransitionsConfigure.Type.ContainerTransform -> materialContainerTransformIn()
                }
            } ?: EnterTransition.None
        },
        exitTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && initialState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.enterRoute && targetRoute == it.exitRoute)
            }
            transition?.let {
                when (transition.type) {
                    TransitionsConfigure.Type.SharedAxisX -> materialSharedAxisXOut(
                        true,
                        slideDistance
                    )

                    TransitionsConfigure.Type.SharedAxisY -> materialSharedAxisYOut(
                        true,
                        slideDistance
                    )

                    TransitionsConfigure.Type.FadeThrough -> materialFadeThroughOut()
                    TransitionsConfigure.Type.ContainerTransform -> materialContainerTransformOut()
                }
            } ?: ExitTransition.None
        },
        popEnterTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.exitRoute && targetRoute == it.enterRoute)
            }
            transition?.let {
                when (transition.type) {
                    TransitionsConfigure.Type.SharedAxisX ->
                        materialSharedAxisXIn(false, slideDistance)

                    TransitionsConfigure.Type.SharedAxisY ->
                        materialSharedAxisYIn(true, slideDistance)

                    TransitionsConfigure.Type.FadeThrough ->
                        materialFadeThroughIn()

                    TransitionsConfigure.Type.ContainerTransform ->
                        materialContainerTransformIn()
                }
            } ?: EnterTransition.None
        },
        popExitTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && initialState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.exitRoute && targetRoute == it.enterRoute)
            }
            transition?.let {
                when (transition.type) {
                    TransitionsConfigure.Type.SharedAxisX ->
                        materialSharedAxisXOut(false, slideDistance)

                    TransitionsConfigure.Type.SharedAxisY ->
                        materialSharedAxisYOut(true, slideDistance)

                    TransitionsConfigure.Type.FadeThrough ->
                        materialFadeThroughOut()

                    TransitionsConfigure.Type.ContainerTransform ->
                        materialContainerTransformOut()
                }
            } ?: ExitTransition.None
        },
    )
}
