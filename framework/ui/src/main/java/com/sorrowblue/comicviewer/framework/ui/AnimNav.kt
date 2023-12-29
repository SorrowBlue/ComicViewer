package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens
import logcat.logcat

fun materialContainerTransformIn(): EnterTransition {
    return materialFadeThroughIn()
}

fun materialContainerTransformOut(): ExitTransition {
    return materialFadeThroughOut()
}

fun materialFadeThroughIn(): EnterTransition =
    fadeIn(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong1,
            easing = MotionTokens.EasingEmphasizedInterpolator,
        ),
        initialAlpha = 0.35f,
    ) + scaleIn(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong1,
            easing = MotionTokens.EasingEmphasizedInterpolator,
        ),
        initialScale = 0.92f,
        transformOrigin = TransformOrigin.Center,
    )

fun materialFadeThroughOut(): ExitTransition =
    fadeOut(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong1,
            easing = MotionTokens.EasingEmphasizedInterpolator,
        ),
        targetAlpha = 0.35f,
    ) + scaleOut(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong1,
            easing = MotionTokens.EasingEmphasizedInterpolator,
        ),
        targetScale = 0.92f,
        transformOrigin = TransformOrigin.Center,
    )

class ComposeValue(
    val navController: NavHostController,
    val windowSizeClass: WindowSizeClass,
    val contentPadding: PaddingValues,
    val slideDistance: Int,
) {
    val isCompact =
        windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact || windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
}

@Composable
fun rememberSlideDistance(): Int {
    val slideDistance: Dp = 30.dp
    val density = LocalDensity.current
    return remember(density, slideDistance) {
        with(density) { slideDistance.roundToPx() }
    }
}

fun materialSharedAxisXIn(
    forward: Boolean,
    slideDistance: Int,
): EnterTransition = slideInHorizontally(
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing,
    ),
    initialOffsetX = {
        if (forward) slideDistance else -slideDistance
    },
) + fadeIn(
    animationSpec = tween(
        durationMillis = 195,
        delayMillis = 105,
        easing = LinearOutSlowInEasing,
    ),
)

fun materialSharedAxisXOut(
    forward: Boolean,
    slideDistance: Int,
): ExitTransition = slideOutHorizontally(
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing,
    ),
    targetOffsetX = {
        if (forward) -slideDistance else slideDistance
    },
) + fadeOut(
    animationSpec = tween(
        durationMillis = 105,
        delayMillis = 0,
        easing = FastOutLinearInEasing,
    ),
)

fun materialSharedAxisYIn(
    slideUp: Boolean,
    slideDistance: Int,
): EnterTransition = slideInVertically(
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing,
    ),
    initialOffsetY = {
        if (slideUp) slideDistance else -slideDistance
    },
) + fadeIn(
    animationSpec = tween(
        durationMillis = 195,
        delayMillis = 105,
        easing = LinearOutSlowInEasing,
    ),
)

fun materialSharedAxisYOut(
    slideDown: Boolean,
    slideDistance: Int,
): ExitTransition = slideOutVertically(
    animationSpec = tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing,
    ),
    targetOffsetY = {
        if (slideDown) slideDistance else -slideDistance
    },
) + fadeOut(
    animationSpec = tween(
        durationMillis = 105,
        delayMillis = 0,
        easing = FastOutLinearInEasing,
    ),
)

@Immutable
class ComposeTransition(
    val enterRoute: String,
    val exitRoute: String?,
    val type: Type,
) {

    enum class Type {
        SharedAxisX,
        SharedAxisY,
        FadeThrough,
        ContainerTransform,
    }
}

context(ComposeValue)
fun NavGraphBuilder.animatedNavigation(
    startDestination: String,
    route: String,
    transitions: List<ComposeTransition>,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    builder: NavGraphBuilder.() -> Unit,
) {
    navigation(
        startDestination = startDestination,
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.enterRoute && targetRoute == it.exitRoute)
            }
            logcat("APPAPP") {
                "$route enter init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX -> materialSharedAxisXIn(true, slideDistance)
                    ComposeTransition.Type.SharedAxisY -> materialSharedAxisYIn(true, slideDistance)
                    ComposeTransition.Type.FadeThrough -> materialFadeThroughIn()
                    ComposeTransition.Type.ContainerTransform -> materialContainerTransformIn()
                }
            } ?: enterTransition?.invoke(this)
        },
        exitTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.enterRoute && targetRoute == it.exitRoute)
            }
            logcat("APPAPP") {
                "$route exit init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX -> materialSharedAxisXOut(
                        true,
                        slideDistance
                    )

                    ComposeTransition.Type.SharedAxisY -> materialSharedAxisYOut(
                        true,
                        slideDistance
                    )

                    ComposeTransition.Type.FadeThrough -> materialFadeThroughOut()
                    ComposeTransition.Type.ContainerTransform -> materialContainerTransformOut()
                }
            } ?: exitTransition?.invoke(this)
        },
        popEnterTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.exitRoute && targetRoute == it.enterRoute)
            }
            logcat("APPAPP") {
                "$route popEnter init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX ->
                        materialSharedAxisXIn(false, slideDistance)

                    ComposeTransition.Type.SharedAxisY ->
                        materialSharedAxisYIn(true, slideDistance)

                    ComposeTransition.Type.FadeThrough ->
                        materialFadeThroughIn()

                    ComposeTransition.Type.ContainerTransform ->
                        materialContainerTransformIn()
                }
            } ?: popEnterTransition?.invoke(this)
        },
        popExitTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.exitRoute && targetRoute == it.enterRoute)
            }
            logcat("APPAPP") {
                "$route popExit init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX ->
                        materialSharedAxisXOut(false, slideDistance)

                    ComposeTransition.Type.SharedAxisY ->
                        materialSharedAxisYOut(true, slideDistance)

                    ComposeTransition.Type.FadeThrough ->
                        materialFadeThroughOut()

                    ComposeTransition.Type.ContainerTransform ->
                        materialContainerTransformOut()
                }
            } ?: popExitTransition?.invoke(this)
        },
        builder = builder
    )
}

context(ComposeValue)
fun NavGraphBuilder.animatedComposable(
    route: String,
    transitions: List<ComposeTransition>,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.enterRoute && targetRoute == it.exitRoute)
            }
            logcat("APPAPP") {
                "$route enter init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX -> materialSharedAxisXIn(true, slideDistance)
                    ComposeTransition.Type.SharedAxisY -> materialSharedAxisYIn(true, slideDistance)
                    ComposeTransition.Type.FadeThrough -> materialFadeThroughIn()
                    ComposeTransition.Type.ContainerTransform -> materialContainerTransformIn()
                }
            } ?: enterTransition?.invoke(this)
        },
        exitTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.enterRoute && targetRoute == it.exitRoute)
            }
            logcat("APPAPP") {
                "$route exit init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX -> materialSharedAxisXOut(
                        true,
                        slideDistance
                    )

                    ComposeTransition.Type.SharedAxisY -> materialSharedAxisYOut(
                        true,
                        slideDistance
                    )

                    ComposeTransition.Type.FadeThrough -> materialFadeThroughOut()
                    ComposeTransition.Type.ContainerTransform -> materialContainerTransformOut()
                }
            } ?: exitTransition?.invoke(this)
        },
        popEnterTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.exitRoute && targetRoute == it.enterRoute)
            }
            logcat("APPAPP") {
                "$route popEnter init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX ->
                        materialSharedAxisXIn(false, slideDistance)

                    ComposeTransition.Type.SharedAxisY ->
                        materialSharedAxisYIn(true, slideDistance)

                    ComposeTransition.Type.FadeThrough ->
                        materialFadeThroughIn()

                    ComposeTransition.Type.ContainerTransform ->
                        materialContainerTransformIn()
                }
            } ?: popEnterTransition?.invoke(this)
        },
        popExitTransition = {
            val initRoute = initialState.destination.route
            val targetRoute = targetState.destination.route
            val transition = transitions.firstOrNull {
                (it.exitRoute == null && targetState.destination.hierarchy.any { a -> a.route == it.enterRoute }) ||
                    (initRoute == it.exitRoute && targetRoute == it.enterRoute)
            }
            logcat("APPAPP") {
                "$route popExit init=${
                    initialState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                } \n target=${
                    targetState.destination.hierarchy.joinToString(",") { it.route.orEmpty() }
                }"
            }
            transition?.let {
                when (transition.type) {
                    ComposeTransition.Type.SharedAxisX ->
                        materialSharedAxisXOut(false, slideDistance)

                    ComposeTransition.Type.SharedAxisY ->
                        materialSharedAxisYOut(true, slideDistance)

                    ComposeTransition.Type.FadeThrough ->
                        materialFadeThroughOut()

                    ComposeTransition.Type.ContainerTransform ->
                        materialContainerTransformOut()
                }
            } ?: popExitTransition?.invoke(this)
        },
        content = content
    )
}
