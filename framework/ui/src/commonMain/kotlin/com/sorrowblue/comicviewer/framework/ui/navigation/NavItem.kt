package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

interface NavItem {
    val title: String
        @Composable
        get
    val icon: ImageVector
}
