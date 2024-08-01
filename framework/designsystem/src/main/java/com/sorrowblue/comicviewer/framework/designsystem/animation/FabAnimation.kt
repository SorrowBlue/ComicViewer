package com.sorrowblue.comicviewer.framework.designsystem.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.TransformOrigin
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens

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

fun AnimatedContentTransitionScope<*>.fabAnimation2(): ContentTransform =
    scaleIn(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationLong2,
            delayMillis = 0,
            easing = MotionTokens.EasingEmphasizedInterpolator
        ),
        initialScale = 0.4f,
        transformOrigin = TransformOrigin(0.5f, 0.5f)
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
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    ) + fadeOut(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort3,
            delayMillis = 0,
            easing = MotionTokens.EasingEmphasizedAccelerateInterpolator
        ),
        targetAlpha = 0f
    ) using SizeTransform(clip = false)
