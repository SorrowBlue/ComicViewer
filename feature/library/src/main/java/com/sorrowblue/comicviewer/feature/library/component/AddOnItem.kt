package com.sorrowblue.comicviewer.feature.library.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowRight
import androidx.compose.material.icons.twotone.ErrorOutline
import androidx.compose.material.icons.twotone.InstallMobile
import androidx.compose.material.icons.twotone.RestartAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

enum class AddOnItemState {
    Still,
    Installing,
    Restart,
    Installed,
    Failed
}

@Composable
internal fun AddOnItem(
    label: Int,
    icon: Int,
    state: AddOnItemState,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text = stringResource(id = label)) },
        leadingContent = {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            when (state) {
                AddOnItemState.Still -> Icon(
                    imageVector = Icons.TwoTone.InstallMobile,
                    contentDescription = null
                )

                AddOnItemState.Installing -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                AddOnItemState.Restart -> Icon(
                    imageVector = Icons.TwoTone.RestartAlt,
                    contentDescription = null
                )

                AddOnItemState.Installed -> Icon(
                    imageVector = Icons.TwoTone.ArrowRight,
                    contentDescription = null
                )

                AddOnItemState.Failed -> Icon(
                    imageVector = Icons.TwoTone.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )

            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
