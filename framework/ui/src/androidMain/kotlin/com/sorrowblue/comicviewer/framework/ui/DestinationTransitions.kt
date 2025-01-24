package com.sorrowblue.comicviewer.framework.ui

/*
@Immutable
data class TransitionsConfigure(
    val enterRoute: RouteOrDirection,
    val exitRoute: RouteOrDirection?,
    val type: Type,
) {

    enum class Type {
        SharedAxisX,
        SharedAxisY,
        FadeThrough,
        ContainerTransform,
    }
}

abstract class DestinationTransitions : DestinationStyle.Animated() {

    companion object {
        var slideDistance = -1
    }

    open val directionToDisplayNavigation: List<DestinationSpec> get() = emptyList()

    abstract val transitions: List<TransitionsConfigure>

    override val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        {
            val initRoute = initialState.destination()
            val targetRoute = targetState.destination()
            val transition = transitions.firstOrNull { configure ->
                (configure.exitRoute == null && targetState.destination.hierarchy.any { it.route == configure.enterRoute.route }) ||
                    (initRoute == configure.enterRoute && targetRoute == configure.exitRoute)
            }
            transition?.let {
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

    override val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        {
            val initRoute = initialState.destination()
            val targetRoute = targetState.destination()
            val transition = transitions.firstOrNull { configure ->
                (configure.exitRoute == null && initialState.destination.hierarchy.any { it.route == configure.enterRoute.route }) ||
                    (initRoute == configure.enterRoute && targetRoute == configure.exitRoute)
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
        }

    override val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        {
            val initRoute = initialState.destination()
            val targetRoute = targetState.destination()
            val transition = transitions.firstOrNull { configure ->
                (configure.exitRoute == null && targetState.destination.hierarchy.any { it.route == configure.enterRoute.route }) ||
                    (initRoute == configure.exitRoute && targetRoute == configure.enterRoute)
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
        }
    override val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        {
            val initRoute = initialState.destination()
            val targetRoute = targetState.destination()
            transitions.firstOrNull { configure ->
                (configure.exitRoute == null && initialState.destination.hierarchy.any { it.route == configure.enterRoute.route }) ||
                    (initRoute == configure.exitRoute && targetRoute == configure.enterRoute)
            }?.let {
                when (it.type) {
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
        }
}
*/
