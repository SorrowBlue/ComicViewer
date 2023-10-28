package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@Composable
fun FilledTonalButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.FilledTonalButton(
        onClick = onClick,
        modifier = modifier
    ) {
        androidx.compose.material3.Text(text = text)
    }
}

@Composable
fun FilledTonalButton(
    onClick: () -> Unit,
    text: Int,
    modifier: Modifier = Modifier,
) {
    FilledTonalButton(
        onClick = onClick,
        text = stringResource(id = text),
        modifier = modifier
    )
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        androidx.compose.material3.Text(text = text)
    }
}


@Composable
fun TextButton(
    onClick: () -> Unit,
    text: Int,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        text = stringResource(id = text),
        modifier = modifier
    )
}
