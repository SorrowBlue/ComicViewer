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
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

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

@Composable
fun materialFadeThroughIn(initialAlpha: Float = 0.0f): EnterTransition = fadeIn(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    initialAlpha = initialAlpha,
) + scaleIn(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    initialScale = 0.92f,
    transformOrigin = TransformOrigin.Center,
)

@Composable
fun materialFadeThroughOut(targetAlpha: Float = 0.0f): ExitTransition = fadeOut(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    targetAlpha = targetAlpha,
) + scaleOut(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    targetScale = 1.1f,
    transformOrigin = TransformOrigin.Center,
)

@Composable
fun materialSharedAxisXIn(): EnterTransition = slideInHorizontally(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    initialOffsetX = { Transitions.slideDistance },
) + materialFadeThroughIn()

@Composable
fun materialSharedAxisXOut(): ExitTransition = slideOutHorizontally(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    targetOffsetX = { -Transitions.slideDistance },
) + materialFadeThroughOut()

@Composable
fun materialSharedAxisYIn(): EnterTransition = slideInVertically(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    initialOffsetY = { Transitions.slideDistance },
) + materialFadeThroughIn()

@Composable
fun materialSharedAxisYOut(): ExitTransition = slideOutVertically(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    targetOffsetY = { Transitions.slideDistance },
) + materialFadeThroughOut()

@Composable
fun materialSharedAxisZIn(): EnterTransition = scaleIn(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    initialScale = 0.8f,
    transformOrigin = TransformOrigin.Center,
) + materialFadeThroughIn()

@Composable
fun materialSharedAxisZOut(): ExitTransition = scaleOut(
    animationSpec = ComicTheme.motionScheme.slowSpatialSpec(),
    targetScale = 1.1f,
    transformOrigin = TransformOrigin.Center,
) + materialFadeThroughOut()
