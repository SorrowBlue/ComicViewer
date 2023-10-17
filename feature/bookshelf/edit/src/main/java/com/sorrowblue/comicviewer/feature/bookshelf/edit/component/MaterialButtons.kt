package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MaterialButtons(
    size: Int,
    label: @Composable (Int) -> Unit,
    selectedIndex: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        for (index in 0..<size) {
            OutlinedButton(
                onClick = { onChange(index) },
                modifier = Modifier
                    .offset((-1 * index).dp, 0.dp),
                shape = if (index == 0) {
                    MaterialTheme.shapes.small.copy(
                        topEnd = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                } else if (index != size - 1) {
                    MaterialTheme.shapes.small.copy(
                        topEnd = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp),
                        topStart = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                } else {
                    MaterialTheme.shapes.small.copy(
                        topStart = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = if (selectedIndex == index) {
                    // selected colors
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    // not selected colors
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                },
            ) {
                label(index)
            }
        }
    }
}
