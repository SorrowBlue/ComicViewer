/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.authentication

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastZip
import androidx.compose.ui.util.lerp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.collections.immutable.persistentListOf

private const val ScaleHalfDurationMillis = 150
private const val ScaleTotalDurationMillis = 300
private const val MorphDelayMillis = 150
private const val MorphDurationMillis = 300
private const val TotalAnimationDurationMillis = 450
private const val ViewportSize = 24f
private const val PivotCenter = 12f
private const val EndScale = 0.5f
private const val DisabledAlpha = 0.38f

@Composable
internal fun PinDrawable(
    index: Int,
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    enabled: Boolean = true,
) {
    val shape = remember(index) {
        PinShapes[index.mod(PinShapes.size)]
    }
    val transitionState = remember {
        MutableTransitionState(!animate).apply {
            targetState = true
        }
    }
    val transition = rememberTransition(transitionState, label = "PinDrawable")
    val scale by transition.animateFloat(
        transitionSpec = {
            keyframes {
                durationMillis = TotalAnimationDurationMillis
                0f at 0
                1f at ScaleHalfDurationMillis using FastOutSlowInEasing
                EndScale at ScaleTotalDurationMillis
                EndScale at TotalAnimationDurationMillis
            }
        },
        label = "scale",
    ) { state ->
        if (state) EndScale else 0f
    }
    val pathProgress by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = MorphDurationMillis,
                delayMillis = MorphDelayMillis,
                easing = FastOutSlowInEasing,
            )
        },
        label = "pathProgress",
    ) { state ->
        if (state) 1f else 0f
    }
    val pathNodes = remember(shape, pathProgress) {
        lerpPathNodes(shape.fromNodes, shape.toNodes, pathProgress)
    }
    val painter = rememberVectorPainter(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = ViewportSize,
        viewportHeight = ViewportSize,
        autoMirror = true,
    ) { _, _ ->
        Group(
            name = "path_group",
            pivotX = PivotCenter,
            pivotY = PivotCenter,
            scaleX = scale,
            scaleY = scale,
        ) {
            Path(
                pathData = pathNodes,
                fill = SolidColor(Color.Black),
            )
        }
    }
    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.graphicsLayer(clip = false),
        colorFilter = ColorFilter.tint(
            if (enabled) {
                ComicTheme.colorScheme.onSurfaceVariant
            } else {
                ComicTheme.colorScheme.onSurface.copy(alpha = DisabledAlpha)
            },
        ),
    )
}

@Immutable
internal class PinShape(fromPathData: String, toPathData: String) {
    val fromNodes: List<PathNode> = PathParser().parsePathString(fromPathData).toNodes()
    val toNodes: List<PathNode> = PathParser().parsePathString(toPathData).toNodes()
}

internal fun lerpPathNodes(
    from: List<PathNode>,
    to: List<PathNode>,
    fraction: Float,
): List<PathNode> = from.fastZip(to) { start, stop ->
    lerpPathNode(start, stop, fraction)
}

private fun lerpPathNode(start: PathNode, stop: PathNode, fraction: Float): PathNode =
    when (start) {
        is PathNode.MoveTo -> {
            require(stop is PathNode.MoveTo)
            PathNode.MoveTo(
                lerp(start.x, stop.x, fraction),
                lerp(start.y, stop.y, fraction),
            )
        }

        is PathNode.LineTo -> {
            require(stop is PathNode.LineTo)
            PathNode.LineTo(
                lerp(start.x, stop.x, fraction),
                lerp(start.y, stop.y, fraction),
            )
        }

        is PathNode.CurveTo -> {
            require(stop is PathNode.CurveTo)
            PathNode.CurveTo(
                lerp(start.x1, stop.x1, fraction),
                lerp(start.y1, stop.y1, fraction),
                lerp(start.x2, stop.x2, fraction),
                lerp(start.y2, stop.y2, fraction),
                lerp(start.x3, stop.x3, fraction),
                lerp(start.y3, stop.y3, fraction),
            )
        }

        PathNode.Close -> PathNode.Close

        else -> error("Unsupported PathNode: $start")
    }

@Suppress("MaxLineLength")
internal val PinShapes = persistentListOf(
    // Favorite (heart) -> Circle
    PinShape(
        fromPathData = "M 13.35 20.13 C 12.59 20.82 11.42 20.82 10.66 20.12 C 10.623 20.087 10.587 20.053 10.55 20.02 C 7.925 17.645 5.755 15.68 4.258 13.824 C 2.76 11.967 1.935 10.22 2 8.28 C 2.06 6.58 2.93 4.95 4.34 3.99 C 5.66 3.09 7.135 2.85 8.5 3.092 C 9.865 3.335 11.12 4.06 12 5.09 C 13.76 3.03 17.02 2.18 19.66 3.99 C 21.07 4.95 21.94 6.58 22 8.28 C 22.07 10.22 21.245 11.967 19.746 13.826 C 18.247 15.685 16.075 17.655 13.45 20.04 C 13.417 20.07 13.383 20.1 13.35 20.13 C 13.35 20.13 13.35 20.13 13.35 20.13",
        toPathData = "M 12 22 C 10.157 22 8.431 21.503 6.95 20.636 C 5.468 19.769 4.231 18.532 3.364 17.05 C 2.497 15.569 2 13.843 2 12 C 2 10.157 2.497 8.431 3.364 6.95 C 4.231 5.468 5.468 4.231 6.95 3.364 C 8.431 2.497 10.157 2 12 2 C 12 2 12 2 12 2 C 13.843 2 15.569 2.497 17.05 3.364 C 18.532 4.231 19.769 5.468 20.636 6.95 C 21.503 8.431 22 10.157 22 12 C 22 13.843 21.503 15.569 20.636 17.05 C 19.769 18.532 18.532 19.769 17.05 20.636 C 15.569 21.503 13.843 22 12 22",
    ),
    // Moon -> Circle
    PinShape(
        fromPathData = "M 11.01 3.05 L 11.01 3.05 C 6.51 3.54 3 7.36 3 12 C 3 16.97 7.03 21 12 21 C 16.63 21 20.45 17.5 20.95 13 C 21.04 12.21 20.17 11.58 19.41 12.05 C 18.57 12.59 17.57 12.9 16.5 12.9 C 13.52 12.9 11.1 10.48 11.1 7.5 C 11.1 6.44 11.41 5.44 11.94 4.61 C 12.39 3.94 11.9 2.98 11.01 3.05",
        toPathData = "M 12 2 L 12 2 C 6.47 2 2 6.47 2 12 C 2 17.53 6.47 22 12 22 C 17.53 22 22 17.53 22 12 C 22 10.894 21.821 9.83 21.491 8.836 C 21.161 7.843 20.679 6.918 20.073 6.091 C 19.467 5.264 18.736 4.533 17.909 3.927 C 17.082 3.321 16.157 2.839 15.164 2.509 C 14.17 2.179 13.106 2 12 2",
    ),
    // Play -> Circle
    PinShape(
        fromPathData = "M 8 6.82 L 8 6.82 C 8 10.273 8 13.727 8 17.18 C 8 17.97 8.87 18.45 9.54 18.02 C 12.253 16.293 14.967 14.567 17.68 12.84 C 18.3 12.45 18.3 11.55 17.68 11.15 C 14.967 9.427 12.253 7.703 9.54 5.98 C 8.87 5.55 8 6.03 8 6.82",
        toPathData = "M 12 2 L 12 2 C 9.235 2 6.735 3.118 4.926 4.926 C 3.118 6.735 2 9.235 2 12 C 2 17.53 6.47 22 12 22 C 17.53 22 22 17.53 22 12 C 22 9.235 20.883 6.735 19.074 4.926 C 17.265 3.118 14.765 2 12 2",
    ),
    // Hexagon -> Circle
    PinShape(
        fromPathData = "M 10.86 2.8 C 8.313 4.58 5.767 6.36 3.22 8.14 C 2.5 8.65 2.19 9.58 2.47 10.42 C 2.47 10.42 2.47 10.42 2.47 10.42 C 3.493 13.493 4.517 16.567 5.54 19.64 C 5.82 20.45 6.58 21 7.44 21 C 8.96 21 10.48 21 12 21 C 13.52 21 15.04 21 16.56 21 C 17.42 21 18.19 20.45 18.46 19.63 C 19.483 16.557 20.507 13.483 21.53 10.41 C 21.81 9.57 21.5 8.65 20.78 8.14 C 18.237 6.36 15.693 4.58 13.15 2.8 C 12.46 2.32 11.54 2.32 10.86 2.8",
        toPathData = "M 12 2 C 9.235 2 6.735 3.118 4.926 4.926 C 4.108 5.745 3.431 6.705 2.935 7.767 C 2.335 9.052 2 10.486 2 12 C 2 13.257 2.231 14.459 2.653 15.566 C 3.648 18.179 5.706 20.265 8.301 21.296 C 9.445 21.75 10.693 22 12 22 C 13.983 22 15.83 21.425 17.383 20.433 C 18.722 19.578 19.842 18.412 20.644 17.037 C 21.506 15.558 22 13.838 22 12 C 22 9.257 20.9 6.775 19.117 4.97 C 17.305 3.136 14.787 2 12 2 C 12 2 12 2 12 2",
    ),
)
