package com.sorrowblue.comicviewer.app.component

import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.app.MainScreenFab

@Composable
internal fun AppFab(
    currentFab: MainScreenFab?,
    canScroll: Boolean,
    onClick: () -> Unit,
) {
    var _currentFab by remember { mutableStateOf<MainScreenFab?>(null) }
    LaunchedEffect(currentFab) {
        if (currentFab != null) {
            _currentFab = currentFab
        }
    }
    ExtendedFloatingActionButton(
        expanded = !canScroll,
        text = {
            if (_currentFab != null) {
                Text(text = stringResource(id = _currentFab!!.label))
            }
        },
        icon = {
            if (_currentFab != null) {
                Icon(
                    imageVector = _currentFab!!.icon,
                    contentDescription = stringResource(id = _currentFab!!.contentDescription)
                )
            }
        },
        onClick = onClick,
    )
}
