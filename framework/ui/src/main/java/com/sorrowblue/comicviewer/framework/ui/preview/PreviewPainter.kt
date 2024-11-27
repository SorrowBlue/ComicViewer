package com.sorrowblue.comicviewer.framework.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource

@Composable
fun previewPainter(): Painter {
    return painterResource(id = nextSampleAvatar)
}

@Composable
fun previewPlaceholder(): Painter? =
    if (LocalInspectionMode.current) {
        previewPainter()
    } else {
        null
    }
