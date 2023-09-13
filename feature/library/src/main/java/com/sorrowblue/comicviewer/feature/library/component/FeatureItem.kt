package com.sorrowblue.comicviewer.feature.library.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@Composable
internal fun BasicItem(label: Int, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(text = stringResource(id = label)) },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
