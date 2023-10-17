package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
fun OverflowMenu(
    state: OverflowMenuState = rememberOverflowMenuState(),
    content: @Composable ColumnScope.() -> Unit,
) {
    IconButton(onClick = state::expand) {
        Icon(ComicIcons.MoreVert, "Open Options")
    }
    DropdownMenu(
        expanded = state.expanded,
        onDismissRequest = state::collapse
    ) {
        content()
    }
}

class OverflowMenuState {
    var expanded by mutableStateOf(false)
        private set

    fun expand() {
        expanded = true
    }

    fun collapse() {
        expanded = false
    }
}

@Composable
fun rememberOverflowMenuState() = remember {
    OverflowMenuState()
}
