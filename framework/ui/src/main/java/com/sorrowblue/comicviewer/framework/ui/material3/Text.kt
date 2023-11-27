package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle

@Composable
fun Text(text: Int, modifier: Modifier = Modifier, style: TextStyle = LocalTextStyle.current) {
    androidx.compose.material3.Text(
        text = stringResource(id = text),
        modifier = modifier,
        style = style
    )
}

@Composable
fun Text(text: String, modifier: Modifier = Modifier, style: TextStyle = LocalTextStyle.current) {
    androidx.compose.material3.Text(text = text, modifier = modifier, style = style)
}

@Composable
fun Button(id: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {
    androidx.compose.material3.Button(modifier = modifier, onClick = onClick) {
        Text(text = id)
    }
}

@Composable
fun RadioButton(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    androidx.compose.material3.RadioButton(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
    )
}
