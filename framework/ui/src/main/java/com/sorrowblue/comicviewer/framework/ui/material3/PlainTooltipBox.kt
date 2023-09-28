package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltipBox(tooltipContent: @Composable () -> Unit, content: @Composable () -> Unit) {
    androidx.compose.material3.PlainTooltipBox(
        tooltip = { tooltipContent() }
    ) {
        Box(modifier = Modifier.tooltipAnchor()) {
            content()
        }
    }
}
