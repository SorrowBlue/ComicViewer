package com.sorrowblue.comicviewer.framework.ui.responsive

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Shape
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize

@Composable
fun CardDefaults.cardColors2(isCompact: Boolean) =
    cardColors(
        containerColor = if (!isCompact) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainerHigh,
    )

@Composable
fun ResponsiveCard(
    modifier: Modifier = Modifier,
    isCompact: Boolean = (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact),
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors2(isCompact),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(modifier, shape, colors, elevation, border, content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsiveCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact),
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors2(isCompact),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}
