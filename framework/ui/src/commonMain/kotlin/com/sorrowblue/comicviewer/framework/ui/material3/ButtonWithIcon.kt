package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.sorrowblue.comicviewer.framework.ui.layout.animatePaddingValues

@Composable
fun ButtonWithIcon(
    onClick: () -> Unit,
    icon: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconEnabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val contentPadding by animatePaddingValues(
        if (iconEnabled) ButtonDefaults.ButtonWithIconContentPadding else ButtonDefaults.ContentPadding
    )
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding
    ) {
        AnimatedVisibility(visible = iconEnabled, label = "ButtonWithIcon") {
            Row {
                icon()
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }
        }
        content()
    }
}

@Composable
fun TextButtonWithIcon(
    onClick: () -> Unit,
    icon: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconEnabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val contentPadding by animatePaddingValues(
        if (iconEnabled) ButtonDefaults.TextButtonWithIconContentPadding else ButtonDefaults.TextButtonContentPadding
    )
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding
    ) {
        AnimatedVisibility(visible = iconEnabled, label = "TextButtonWithIcon") {
            Row {
                icon()
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            }
        }
        content()
    }
}
