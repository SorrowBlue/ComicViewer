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
    var mainScreenFab by remember { mutableStateOf<MainScreenFab?>(null) }
    LaunchedEffect(currentFab) {
        if (currentFab != null) {
            mainScreenFab = currentFab
        }
    }
    ExtendedFloatingActionButton(
        expanded = !canScroll,
        text = {
            if (mainScreenFab != null) {
                Text(text = stringResource(id = mainScreenFab!!.label))
            }
        },
        icon = {
            if (mainScreenFab != null) {
                Icon(
                    imageVector = mainScreenFab!!.icon,
                    contentDescription = stringResource(id = mainScreenFab!!.contentDescription)
                )
            }
        },
        onClick = onClick,
    )
}
