package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.animation.core.animate
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.ProgressIndicatorDefaults.drawStopIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearPullRefreshContainer(
    pullRefreshState: PullToRefreshState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    var progress by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(pullRefreshState.distanceFraction) {
        if (pullRefreshState.distanceFraction == 0f) {
            animate(progress, 0f) { value, _ ->
                progress = value
            }
        } else {
            progress = pullRefreshState.distanceFraction
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullRefreshState,
                isRefreshing = pullRefreshState.isAnimating,
                onRefresh = { /*TODO()*/ }
            )
    ) {
        content()
        if (pullRefreshState.isAnimating) {
            LinearProgressIndicator(
                trackColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        } else if (0 < progress) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearPullRefreshContainer(
    pullRefreshState: PullToRefreshState,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val progress by remember {
        derivedStateOf { pullRefreshState.distanceFraction > 0 }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullToRefresh(
                state = pullRefreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            )
    ) {
        content()
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Butt,
                gapSize = 0.dp,
            )
        } else if (progress) {
            val color = ProgressIndicatorDefaults.linearColor
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPadding),
                progress = { pullRefreshState.distanceFraction },
                strokeCap = StrokeCap.Butt,
                gapSize = 1.dp,
                drawStopIndicator = {
                    drawStopIndicator(
                        drawScope = this,
                        stopSize = 0.dp,
                        color = color,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                    )
                }
            )
        }
    }
}
