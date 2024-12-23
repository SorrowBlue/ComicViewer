package com.sorrowblue.comicviewer.framework.ui.adaptive

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState

@Composable
fun CanonicalTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    bottomComponent: @Composable ColumnScope.() -> Unit = {},
    scrollableState: ScrollableState? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val colors = if (LocalNavigationState.current is NavigationState.NavigationBar) {
        TopAppBarDefaults.topAppBarColors()
    } else {
        TopAppBarDefaults.topAppBarColors().run {
            TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = containerColor
            )
        }
    }
    val targetColor by
        remember(colors, scrollBehavior) {
            derivedStateOf {
                val overlappingFraction = scrollBehavior?.state?.overlappedFraction ?: 0f
                colors.containerColor(if (overlappingFraction > 0.01f) 1f else 0f)
            }
        }

    val appBarContainerColor = animateColorAsState(
        targetColor,
        animationSpec = spring(dampingRatio = 1.0f, stiffness = 1600.0f),
        label = "appBarContainerColor"
    )
    Column(
        modifier = modifier.drawBehind {
            val color = appBarContainerColor.value
            if (color != Color.Unspecified) {
                drawRect(color = color)
            }
        }
    ) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = colors,
            scrollBehavior = scrollBehavior,
            windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        )
        bottomComponent()
        if (scrollableState != null && LocalNavigationState.current !is NavigationState.NavigationBar) {
            if (scrollableState.canScrollBackward) {
                HorizontalDivider()
            }
        }
    }
}

@Stable
internal fun TopAppBarColors.containerColor(colorTransitionFraction: Float): Color {
    return lerp(
        containerColor,
        scrolledContainerColor,
        FastOutLinearInEasing.transform(colorTransitionFraction)
    )
}
