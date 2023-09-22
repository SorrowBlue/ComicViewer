package com.sorrowblue.comicviewer.framework.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun PaddingValues.toWindowInsets(localLayoutDirection: LayoutDirection = LocalLayoutDirection.current) =
    WindowInsets(
        left = calculateLeftPadding(localLayoutDirection),
        top = calculateTopPadding(),
        right = calculateRightPadding(localLayoutDirection),
        bottom = calculateBottomPadding()
    )
