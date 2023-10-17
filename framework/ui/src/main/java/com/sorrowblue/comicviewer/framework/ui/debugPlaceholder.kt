package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalInspectionMode
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawZoomIn

@Composable
fun debugPlaceholder(): Painter? =
    if (LocalInspectionMode.current) {
        rememberVectorPainter(image = ComicIcons.UndrawZoomIn)
    } else {
        null
    }
