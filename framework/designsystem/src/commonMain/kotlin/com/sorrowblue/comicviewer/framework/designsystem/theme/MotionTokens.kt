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

    /**
     * 画面に入る
     *
     * これらのトランジションでは、強調された減速イージングが使用されています。 ピーク速度で始まり、その後緩やかに停止します。
     */
    val EasingEmphaizedDecelerateInterpolator = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)

    /**
     * 画面から完全に終了する
     *
     * これらのトランジションでは、強調された加速イージングが使用されています。 静止状態から始まり、最高速度で終了します。
     * 最高速度で終了することで、終了したコンポーネントを回復できないという印象を与えます。
     */
    val EasingEmphasizedAccelerateInterpolator = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

    /**
     * 一時的に画面を終了する
     *
     * これらのトランジションでは、強調されたイージングが使用されています。
     * 画面外で静止した状態で終了することで、終了したコンポーネントを復元できるような印象を与えます。
     */
    val EasingEmphasizedInterpolator = PathEasing(
        Path().apply {
            moveTo(0f, 0f)
            cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
            cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
        },
    )

    val EasingEmphasized = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EasingEmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EasingEmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val EasingStandard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val EasingStandardDecelerate = CubicBezierEasing(0.0f, 0.0f, 0.0f, 1.0f)
    val EasingStandardAccelerate = CubicBezierEasing(0.3f, 0.0f, 1.0f, 1.0f)

    const val DurationEmphasized = 500
    const val DurationEmphasizedDecelerate = 400
    const val DurationEmphasizedAccelerate = 200
    const val DurationStandard = 300
    const val DurationStandardDecelerate = 250
    const val DurationStandardAccelerate = 200
}
