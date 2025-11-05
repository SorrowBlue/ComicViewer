package com.sorrowblue.comicviewer.app

import androidx.compose.ui.graphics.vector.ImageVector
import com.sorrowblue.comicviewer.framework.ui.navigation.NavItem

internal sealed class AppNavItem(
    val navGraph: Any,
    override val icon: ImageVector,
) : NavItem
