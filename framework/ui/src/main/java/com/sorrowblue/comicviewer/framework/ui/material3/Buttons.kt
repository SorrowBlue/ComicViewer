package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.R

@Composable
fun SettingsIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = ComicIcons.Settings,
            contentDescription = stringResource(R.string.ui_desc_open_settings)
        )
    }
}

@Composable
fun BackIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = ComicIcons.ArrowBack,
            contentDescription = stringResource(R.string.ui_desc_move_up)
        )
    }
}

@Composable
fun CloseIconButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = ComicIcons.Close,
            contentDescription = stringResource(R.string.ui_desc_close)
        )
    }
}
