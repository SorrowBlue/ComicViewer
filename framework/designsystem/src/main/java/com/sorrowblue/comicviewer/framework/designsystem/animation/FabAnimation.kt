package com.sorrowblue.comicviewer.framework.designsystem.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.TransformOrigin
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens
import kotlin.math.roundToInt

fun AnimatedContentTransitionScope<*>.fabAnimation(): ContentTransform =
    scaleIn(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong2,
            delayMillis = 0,
            easing = MotionTokens.EasingEmphasizedInterpolator
        ),
        initialScale = 0.4f,
        transformOrigin = TransformOrigin(1f, 1f)
    ) + fadeIn(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong2,
            delayMillis = 0,
            easing = MotionTokens.EasingEmphasizedInterpolator
        ),
        initialAlpha = 0f
    ) togetherWith scaleOut(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium1,
            delayMillis = 0,
            easing = MotionTokens.EasingEmphasizedAccelerateInterpolator
        ),
        targetScale = 0.0f,
        transformOrigin = TransformOrigin(1f, 1f)
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort3,
            delayMillis = 0,
            easing = MotionTokens.EasingEmphasizedAccelerateInterpolator
        ),
        targetAlpha = 0f
    ) using SizeTransform(clip = false)

fun AnimatedContentTransitionScope<*>.navigationBarAnimation() =
    slideInVertically(
        animationSpec = tween(
            MotionTokens.DurationMedium4,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        initialOffsetY = { (it * 0.2).roundToInt() }
    ) + fadeIn(
        animationSpec = tween(
            MotionTokens.DurationMedium4,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        0f,
    ) togetherWith slideOutVertically(
        animationSpec = tween(
            MotionTokens.DurationMedium3,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        targetOffsetY = { (it * 0.2).roundToInt() }
    ) + fadeOut(
        animationSpec = tween(
            MotionTokens.DurationMedium2,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        0f,
    ) using SizeTransform(clip = false)

fun AnimatedContentTransitionScope<*>.navigationRailAnimation() =
    slideInHorizontally(
        animationSpec = tween(
            MotionTokens.DurationMedium4,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        initialOffsetX = { 0 }
    ) + fadeIn(
        animationSpec = tween(
            MotionTokens.DurationMedium4,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        0f,
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(
            MotionTokens.DurationMedium3,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        targetOffsetX = { 0 }
    ) + fadeOut(
        animationSpec = tween(
            MotionTokens.DurationMedium2,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        0f,
    ) using SizeTransform(clip = false)
