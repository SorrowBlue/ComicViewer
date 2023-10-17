package com.sorrowblue.comicviewer.framework.designsystem.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import com.sorrowblue.comicviewer.framework.designsystem.theme.MotionTokens

fun AnimatedContentTransitionScope<*>.navigationRailAnimation() =
    slideInHorizontally(
        animationSpec = tween(
            MotionTokens.DurationMedium4,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        initialOffsetX = { -it }
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
        targetOffsetX = { -it }
    ) + fadeOut(
        animationSpec = tween(
            MotionTokens.DurationMedium2,
            0,
            MotionTokens.EasingEmphasizedInterpolator
        ),
        0f,
    ) using SizeTransform(clip = false)
