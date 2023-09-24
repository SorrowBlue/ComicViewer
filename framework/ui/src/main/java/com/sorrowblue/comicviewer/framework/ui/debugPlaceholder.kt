package com.sorrowblue.comicviewer.framework.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource

@Composable
fun debugPlaceholder(): Painter? =
    if (LocalInspectionMode.current) {
        painterResource(id = R.drawable.ic_undraw_zoom_in__1_txs)
    } else {
        null
    }
