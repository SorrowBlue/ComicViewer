package com.sorrowblue.comicviewer.feature.tutorial

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val ROTATION_ANIMATION_DURATION = 4000
private const val SWEEP_ANIMATION_DURATION = 4000

@Suppress("ModifierComposed")
internal fun Modifier.immatureRectangleProgressBorder(
    strokeWidth: Dp = 5.dp,
    cornerRadius: Dp = 12.dp,
    color: Color = Color.Blue,
    enable: Boolean = false,
): Modifier = composed {
// --- 状態変化に応じたトランジションアニメーション ---
    // enabledがtrueなら1.0f、falseなら0.0fにアニメーションする値
    val visibilityFactor by animateFloatAsState(
        targetValue = if (enable) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 500),
        label = "visibility_factor",
    )

    // visibilityFactorが0の場合は描画自体をスキップ
    if (visibilityFactor == 0f) return@composed this

    val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }
    val cornerRadiusPx = with(LocalDensity.current) { cornerRadius.toPx() }

    // --- 無限ループアニメーション ---
    // これらはenabled状態に関わらず裏で動き続けるが、描画はvisibilityFactorに依存する
    val infiniteTransition = rememberInfiniteTransition(label = "immature_border_transition")

    val startPositionRatio by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(ROTATION_ANIMATION_DURATION, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "start_position",
    )

    val sweepLengthRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(SWEEP_ANIMATION_DURATION, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sweep_length",
    )

    // --- 描画のための準備 ---
    val pathMeasure = remember { PathMeasure() }
    val path = remember { Path() }
    val progressPath = remember { Path() }

    this
        .drawBehind {
            val halfStroke = strokeWidthPx / 2
            path.reset()
            path.addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(halfStroke, halfStroke),
                        size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
                    ),
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                ),
            )
            pathMeasure.setPath(path, false)
            val totalLength = pathMeasure.length

            // --- 始点と終点を計算 ---
            // ★★★ 長さにvisibilityFactorを掛けることで、無効化時に長さが0になるアニメーションを実現 ★★★
            val currentSweepLength = totalLength * sweepLengthRatio * visibilityFactor
            val startDistance = totalLength * startPositionRatio
            val endDistance = startDistance + currentSweepLength

            progressPath.reset()
            if (endDistance > totalLength) {
                pathMeasure.getSegment(
                    startDistance,
                    totalLength,
                    progressPath,
                    startWithMoveTo = true,
                )
                pathMeasure.getSegment(
                    0f,
                    endDistance - totalLength,
                    progressPath,
                    startWithMoveTo = true,
                )
            } else {
                pathMeasure.getSegment(
                    startDistance,
                    endDistance,
                    progressPath,
                    startWithMoveTo = true,
                )
            }

            // 1. 背景のうすーいパス（ガイド線）もフェードアウトさせる
            drawPath(
                path = path,
                color = Color.LightGray.copy(alpha = 0.4f * visibilityFactor),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )

            // 2. 主役の進捗パス
            drawPath(
                path = progressPath,
                color = color,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round),
            )
        }
}
