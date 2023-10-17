package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun Text(id: Int, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = stringResource(id = id), modifier = modifier)
}
