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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion

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

fun materialSharedAxisXIn(
    forward: Boolean,
    slideDistance: Int,
): EnterTransition = slideInHorizontally(
    animationSpec = ExpressiveMotion.Spatial.slow(),
    initialOffsetX = {
        if (forward) slideDistance else -slideDistance
    },
) + materialFadeThroughIn()

fun materialSharedAxisXOut(
    forward: Boolean,
    slideDistance: Int,
): ExitTransition = slideOutHorizontally(
    animationSpec = ExpressiveMotion.Spatial.slow(),
    targetOffsetX = {
        if (forward) -slideDistance else slideDistance
    },
) + materialFadeThroughOut()

fun materialSharedAxisYIn(
    slideUp: Boolean,
    slideDistance: Int,
): EnterTransition = slideInVertically(
    animationSpec = ExpressiveMotion.Spatial.slow(),
    initialOffsetY = {
        if (slideUp) slideDistance else -slideDistance
    },
) + materialFadeThroughIn()

fun materialSharedAxisYOut(
    slideDown: Boolean,
    slideDistance: Int,
): ExitTransition = slideOutVertically(
    animationSpec = ExpressiveMotion.Spatial.slow(),
    targetOffsetY = {
        if (slideDown) slideDistance else -slideDistance
    },
) + materialFadeThroughOut()

@Composable
fun rememberSlideDistance(): Int {
    val slideDistance: Dp = 30.dp
    val density = LocalDensity.current
    return remember(density, slideDistance) {
        with(density) { slideDistance.roundToPx() }
    }
}
