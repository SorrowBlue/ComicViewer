package com.sorrowblue.comicviewer.framework.ui.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.NavDisplay
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

object Transitions {
    var slideDistance = -1
    lateinit var motionScheme: MotionScheme

    @Composable
    fun InitSlideDistance() {
        val slideDistanceDp = 30.dp
        val density = LocalDensity.current
        slideDistance = remember(density, slideDistanceDp) {
            with(density) { slideDistanceDp.roundToPx() }
        }
    }
}

fun materialSharedAxisX(forward: Boolean): ContentTransform = materialSharedAxisXIn(
    forward = forward,
) togetherWith materialSharedAxisXOut(forward = forward)

fun materialSharedAxisZ(): ContentTransform =
    materialSharedAxisZIn() togetherWith materialSharedAxisZOut()

fun materialFadeThrough(): ContentTransform =
    materialFadeThroughIn(Transitions.motionScheme) togetherWith
        materialFadeThroughOut(Transitions.motionScheme)

fun NavDisplay.transitionMaterialFadeThrough(): Map<String, Any> = NavDisplay.transitionSpec {
    materialFadeThrough()
} +
    NavDisplay.popTransitionSpec { materialFadeThrough() } +
    NavDisplay.predictivePopTransitionSpec { materialFadeThrough() }

fun NavDisplay.transitionMaterialSharedAxisX(): Map<String, Any> = NavDisplay.transitionSpec {
    materialSharedAxisX(true)
} +
    NavDisplay.popTransitionSpec { materialSharedAxisX(false) } +
    NavDisplay.predictivePopTransitionSpec { materialSharedAxisX(false) }

fun NavDisplay.transitionMaterialSharedAxisZ(): Map<String, Any> = NavDisplay.transitionSpec {
    materialSharedAxisZ()
} +
    NavDisplay.popTransitionSpec { materialSharedAxisZ() } +
    NavDisplay.predictivePopTransitionSpec { materialSharedAxisZ() }

@Composable
fun materialFadeThroughIn(initialAlpha: Float = 0.0f): EnterTransition =
    materialFadeThroughIn(ComicTheme.motionScheme, initialAlpha)

fun materialFadeThroughIn(
    motionScheme: MotionScheme,
    initialAlpha: Float = 0.0f,
): EnterTransition = fadeIn(
    animationSpec = motionScheme.slowSpatialSpec(),
    initialAlpha = initialAlpha,
) + scaleIn(
    animationSpec = motionScheme.slowSpatialSpec(),
    initialScale = 0.92f,
    transformOrigin = TransformOrigin.Center,
)

@Composable
fun materialFadeThroughOut(targetAlpha: Float = 0.0f): ExitTransition =
    materialFadeThroughOut(ComicTheme.motionScheme, targetAlpha)

fun materialFadeThroughOut(
    motionScheme: MotionScheme = Transitions.motionScheme,
    targetAlpha: Float = 0.0f,
): ExitTransition = fadeOut(
    animationSpec = motionScheme.slowSpatialSpec(),
    targetAlpha = targetAlpha,
) + scaleOut(
    animationSpec = motionScheme.slowSpatialSpec(),
    targetScale = 1.1f,
    transformOrigin = TransformOrigin.Center,
)

fun materialSharedAxisXIn(
    motionScheme: MotionScheme = Transitions.motionScheme,
    forward: Boolean = true,
): EnterTransition = slideInHorizontally(
    animationSpec = motionScheme.slowSpatialSpec(),
    initialOffsetX = { if (forward) Transitions.slideDistance else -Transitions.slideDistance },
) + materialFadeThroughIn(motionScheme)

fun materialSharedAxisXOut(
    motionScheme: MotionScheme = Transitions.motionScheme,
    forward: Boolean = true,
): ExitTransition = slideOutHorizontally(
    animationSpec = motionScheme.slowSpatialSpec(),
    targetOffsetX = { if (forward) -Transitions.slideDistance else Transitions.slideDistance },
) + materialFadeThroughOut(motionScheme)

fun materialSharedAxisYIn(
    motionScheme: MotionScheme = Transitions.motionScheme,
    forward: Boolean = true,
): EnterTransition = slideInVertically(
    animationSpec = motionScheme.slowSpatialSpec(),
    initialOffsetY = { if (forward) Transitions.slideDistance else -Transitions.slideDistance },
) + materialFadeThroughIn(motionScheme)

fun materialSharedAxisYOut(
    motionScheme: MotionScheme = Transitions.motionScheme,
    forward: Boolean = true,
): ExitTransition = slideOutVertically(
    animationSpec = motionScheme.slowSpatialSpec(),
    targetOffsetY = { if (forward) -Transitions.slideDistance else Transitions.slideDistance },
) + materialFadeThroughOut(motionScheme)

fun materialSharedAxisZIn(motionScheme: MotionScheme = Transitions.motionScheme): EnterTransition =
    scaleIn(
        animationSpec = motionScheme.slowSpatialSpec(),
        initialScale = 0.8f,
        transformOrigin = TransformOrigin.Center,
    ) + materialFadeThroughIn(motionScheme)

fun materialSharedAxisZOut(motionScheme: MotionScheme = Transitions.motionScheme): ExitTransition =
    scaleOut(
        animationSpec = motionScheme.slowSpatialSpec(),
        targetScale = 1.1f,
        transformOrigin = TransformOrigin.Center,
    ) + materialFadeThroughOut(motionScheme)
