package com.sorrowblue.comicviewer.file.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.file.generated.resources.Res
import comicviewer.feature.file.generated.resources.file_info_label_add_read_later
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ReadlaterButton(
    checked: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ActionButton(
        modifier = modifier,
        onClick = onClick,
        icon = {
            Icon(ComicIcons.WatchLater, null)
        },
        text = {
            if (checked) {
                Text(text = "後で読むから削除")
            } else {
                Text(text = stringResource(Res.string.file_info_label_add_read_later))
            }
        },
        enabled = !loading,
        loading = loading,
    )
}

@Composable
fun RotatingBorderAnimation(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 4.dp,
    borderColor: Color = Color.Blue,
    segmentLengthRatio: Float = 0.25f, // 例: 1/4周分の線を表示
    durationMillis: Int = 2000,
    strokeCap: StrokeCap = StrokeCap.Butt, // 端を四角くしたい場合は Square や Butt
) {
    // 無限ループアニメーションの設定
    val infiniteTransition = rememberInfiniteTransition(label = "RotatingBorder")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f, // 0f から 1f で1周を表す
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart, // 繰り返しの種類 (Restart: 最初から)
        ),
        label = "BorderProgress",
    )

    // パス操作のためのオブジェクト (状態として保持し、再利用)
    val pathMeasure = remember { PathMeasure() }
    val path = remember { Path() } // 四角形全体のパス
    val segmentPath = remember { Path() } // 描画する線分のパス

    Canvas(modifier = modifier) {
        val strokeWidthPx = borderWidth.toPx()
        // 描画領域のサイズから、線の太さの半分を考慮した内側の矩形を計算
        val inset = strokeWidthPx / 2f
        val innerRect = Rect(
            left = inset,
            top = inset,
            right = this.size.width - inset,
            bottom = this.size.height - inset,
        )

        // 1. 四角形のパスを作成
        path.reset() // 再利用のためにリセット
        path.addRect(innerRect)
        // path.close() // addRectは自動的に閉じるが、明示しても良い

        // 2. PathMeasureにパスを設定
        pathMeasure.setPath(path, false) // パスが閉じている場合は false
        val pathLength = pathMeasure.length

        // 3. 表示する線分の長さを計算
        val segmentLength = pathLength * segmentLengthRatio.coerceIn(0f, 1f)

        // 4. アニメーションの進捗に基づいて、線分の開始位置と終了位置を計算
        //    (pathLength で剰余を取ることで、0 ~ pathLength の範囲に収める)
        val startDistance = (progress * pathLength) % pathLength
        val endDistance = (startDistance + segmentLength) % pathLength

        // 5. PathMeasureから線分を抽出
        segmentPath.reset() // 線分パスをリセット

        if (startDistance < endDistance) {
            // 通常の場合 (線分が一周をまたがない)
            pathMeasure.getSegment(
                startDistance = startDistance,
                stopDistance = endDistance,
                destination = segmentPath,
                startWithMoveTo = true, // 新しいパスの開始点をMoveToにする
            )
        } else {
            // 線分がパスの終点と始点をまたぐ場合
            // a. 開始位置からパスの終点まで
            pathMeasure.getSegment(startDistance, pathLength, segmentPath, true)
            // b. パスの始点から終了位置まで (前のセグメントに続けて描画)
            pathMeasure.getSegment(0f, endDistance, segmentPath, true)
        }

        // 6. 抽出した線分パスを描画
        drawPath(
            path = segmentPath,
            color = borderColor,
            style = Stroke(
                width = strokeWidthPx,
                cap = strokeCap, // 線の端の形状
                join = StrokeJoin.Round, // 角の形状 (Miter: 尖る, Round: 丸める, Bevel: 斜めにカット)
            ),
        )
    }
}

// --- プレビュー ---
@Preview
@Composable
private fun RotatingBorderAnimationPreview() {
    Box(
        modifier = Modifier.padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        RotatingBorderAnimation(
            borderWidth = 8.dp,
            borderColor = Color.Magenta,
            segmentLengthRatio = 0.3f,
            durationMillis = 3000,
            strokeCap = StrokeCap.Round, // 端を丸める
        )
    }
}
