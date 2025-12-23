package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

@Composable
fun SettingsIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.testTag("SettingsButton"),
    ) {
        Icon(
            imageVector = ComicIcons.Settings,
            contentDescription = null,
        )
    }
}

@Composable
fun BackIconButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.testTag("BackButton"),
    ) {
        Icon(
            imageVector = ComicIcons.ArrowBack,
            contentDescription = null,
        )
    }
}

@Composable
fun CloseIconButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true,) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.testTag("CloseButton"),
    ) {
        Icon(
            imageVector = ComicIcons.Close,
            contentDescription = null,
        )
    }
}
