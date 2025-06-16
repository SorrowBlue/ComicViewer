package com.sorrowblue.comicviewer.framework.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ExpressiveMotion
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens

fun materialContainerTransformIn(): EnterTransition {
    return materialFadeThroughIn()
}

fun materialContainerTransformOut(): ExitTransition {
    return materialFadeThroughOut()
}

fun materialFadeThroughIn(): EnterTransition {
    return fadeIn(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        initialAlpha = 0.0f,
    ) + scaleIn(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        initialScale = 0.92f,
        transformOrigin = TransformOrigin.Center,
    )
}

fun materialFadeThroughOut(): ExitTransition {
    return fadeOut(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        targetAlpha = 0.0f,
    ) + scaleOut(
        animationSpec = ExpressiveMotion.Spatial.slow(),
        targetScale = 0.0f,
        transformOrigin = TransformOrigin.Center,
    )
}

fun materialSharedAxisXIn(
    forward: Boolean,
    slideDistance: Int,
): EnterTransition = slideInHorizontally(
    animationSpec = tween(
        durationMillis = 400,
        easing = MotionTokens.EasingEmphasizedDecelerate,
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
        durationMillis = 200,
        easing = MotionTokens.EasingEmphasizedAccelerate,
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
        durationMillis = 400,
        easing = MotionTokens.EasingEmphasizedDecelerate,
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
        durationMillis = 200,
        easing = MotionTokens.EasingEmphasizedAccelerate,
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

@Composable
fun rememberSlideDistance(): Int {
    val slideDistance: Dp = 30.dp
    val density = LocalDensity.current
    return remember(density, slideDistance) {
        with(density) { slideDistance.roundToPx() }
    }
}
