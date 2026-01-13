package com.sorrowblue.comicviewer.framework.designsystem.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import com.sorrowblue.comicviewer.framework.designsystem.tokens.MotionTokens
import kotlin.math.roundToInt

fun AnimatedContentTransitionScope<*>.topAppBarAnimation() = slideInVertically(
    animationSpec = tween(
        MotionTokens.DurationMedium4,
        0,
        MotionTokens.EasingEmphasizedInterpolator,
    ),
    initialOffsetY = { (-it * 0.2).roundToInt() },
) + fadeIn(
    animationSpec = tween(
        MotionTokens.DurationMedium4,
        0,
        MotionTokens.EasingEmphasizedInterpolator,
    ),
    0f,
) togetherWith slideOutVertically(
    animationSpec = tween(
        MotionTokens.DurationMedium3,
        0,
        MotionTokens.EasingEmphasizedInterpolator,
    ),
    targetOffsetY = { (-it * 0.2).roundToInt() },
) + fadeOut(
    animationSpec = tween(
        MotionTokens.DurationMedium2,
        0,
        MotionTokens.EasingEmphasizedInterpolator,
    ),
    0f,
) using SizeTransform(clip = false)
