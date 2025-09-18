package com.sorrowblue.comicviewer.framework.ui.animation

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion

object Transitions {
    var slideDistance = -1

    @Composable
    fun InitSlideDistance() {
        val slideDistanceDp = 30.dp
        val density = LocalDensity.current
        slideDistance = remember(density, slideDistanceDp) {
            with(density) { slideDistanceDp.roundToPx() }
        }
    }

}

fun materialContainerTransformIn(): EnterTransition {
    return materialFadeThroughIn()
}

fun materialContainerTransformOut(): ExitTransition {
    return materialFadeThroughOut()
}

fun materialFadeThroughIn(initialAlpha: Float = 0.0f): EnterTransition {
    return fadeIn(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        initialAlpha = initialAlpha,
    ) + scaleIn(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        initialScale = 0.92f,
        transformOrigin = TransformOrigin.Center,
    )
}

fun materialFadeThroughOut(targetAlpha: Float = 0.0f): ExitTransition {
    return fadeOut(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        targetAlpha = targetAlpha,
    ) + scaleOut(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        targetScale = 1.1f,
        transformOrigin = TransformOrigin.Center,
    )
}

fun materialSharedAxisXIn(): EnterTransition =
    slideInHorizontally(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        initialOffsetX = { Transitions.slideDistance },
    ) + materialFadeThroughIn()

fun materialSharedAxisXOut(): ExitTransition =
    slideOutHorizontally(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        targetOffsetX = { -Transitions.slideDistance },
    ) + materialFadeThroughOut()

fun materialSharedAxisYIn(): EnterTransition =
    slideInVertically(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        initialOffsetY = { Transitions.slideDistance },
    ) + materialFadeThroughIn()

fun materialSharedAxisYOut(): ExitTransition =
    slideOutVertically(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        targetOffsetY = { Transitions.slideDistance },
    ) + materialFadeThroughOut()

fun materialSharedAxisZIn(): EnterTransition = scaleIn(
    animationSpec = ExpressiveMotion.Spatial.slow(),
    initialScale = 0.8f,
    transformOrigin = TransformOrigin.Center,
) + materialFadeThroughIn()

fun materialSharedAxisZOut(): ExitTransition = scaleOut(
    animationSpec = ExpressiveMotion.Spatial.slow(),
    targetScale = 1.1f,
    transformOrigin = TransformOrigin.Center,
) + materialFadeThroughOut()
