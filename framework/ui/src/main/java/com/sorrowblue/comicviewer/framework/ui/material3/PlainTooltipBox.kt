package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltipBox(tooltipContent: @Composable () -> Unit, content: @Composable () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = tooltipContent,
        state = rememberTooltipState()
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltipBox2(
    tooltipContent: @Composable () -> Unit,
    content: @Composable (TooltipState) -> Unit,
) {
    val tooltipState: TooltipState = rememberTooltipState()
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = tooltipContent,
        state = tooltipState
    ) {
        content(tooltipState)
    }
}
