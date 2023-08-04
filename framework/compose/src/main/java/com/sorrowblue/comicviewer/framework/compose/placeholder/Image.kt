package com.sorrowblue.comicviewer.framework.compose.placeholder

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import com.sorrowblue.comicviewer.framework.compose.R

@Composable
fun debugPlaceholder(): Painter? =
    if (LocalInspectionMode.current) {
        painterResource(id = R.drawable.avatar_1)
    } else {
        null
    }
