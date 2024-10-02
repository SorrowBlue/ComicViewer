package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlainTooltipBox(
    tooltipContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip(
                modifier = Modifier.padding(4.dp),
                content = tooltipContent
            )
        },
        state = rememberTooltipState(),
        modifier = modifier
    ) {
        content()
    }
}
