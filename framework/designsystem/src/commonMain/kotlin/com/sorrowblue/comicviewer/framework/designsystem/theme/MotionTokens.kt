package com.sorrowblue.comicviewer.framework.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.PathEasing
import androidx.compose.ui.graphics.Path

object MotionTokens {
    const val DurationExtraLong1 = 700
    const val DurationExtraLong2 = 800
    const val DurationExtraLong3 = 900
    const val DurationExtraLong4 = 1000
    const val DurationLong1 = 450
    const val DurationLong2 = 500
    const val DurationLong3 = 550
    const val DurationLong4 = 600
    const val DurationMedium1 = 250
    const val DurationMedium2 = 300
    const val DurationMedium3 = 350
    const val DurationMedium4 = 400
    const val DurationShort1 = 50
    const val DurationShort2 = 100
    const val DurationShort3 = 150
    const val DurationShort4 = 200

    val EasingEmphasizedInterpolator = PathEasing(
        Path().apply {
            moveTo(0f, 0f)
            cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
            cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
        }
    )
    val EasingEmphasizedAccelerateInterpolator = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)
    val EasingEmphaizedDecelerateInterpolator = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EasingEmphasizedCubicBezier = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
}
