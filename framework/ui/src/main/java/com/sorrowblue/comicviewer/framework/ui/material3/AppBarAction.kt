package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.collections.immutable.PersistentList

interface AppBarAction {
    val label: String
    val icon: ImageVector
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : AppBarAction> TopAppBar(
    title: @Composable () -> Unit,
    actions: PersistentList<T>,
    onClick: (T) -> Unit,
) {
    androidx.compose.material3.TopAppBar(title = title, actions = {
        if (actions.size <= 3) {
            actions.forEach { action ->
                key(action.label) {
                    PlainTooltipBox(tooltipContent = {
                        Text(text = action.label)
                    }) {
                        IconButton(onClick = { onClick.invoke(action) }) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label
                            )
                        }
                    }
                }
            }
        } else {
            actions.take(2).forEach { action ->
                key(action.label) {
                    PlainTooltipBox(tooltipContent = { Text(text = action.label) }) {
                        IconButton(onClick = { onClick.invoke(action) }) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label
                            )
                        }
                    }
                }
            }
            val overflowMenuState = rememberOverflowMenuState()
            OverflowMenu(overflowMenuState) {
                actions.drop(2).forEach { action ->
                    key(action.label) {
                        DropdownMenuItem(
                            text = { Text(action.label) },
                            trailingIcon = { Icon(action.icon, action.label) },
                            onClick = {
                                overflowMenuState.collapse()
                                onClick.invoke(action)
                            }
                        )
                    }
                }
            }
        }
    })
}
