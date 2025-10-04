package com.sorrowblue.comicviewer.framework.ui.navigation

import androidx.compose.ui.window.DialogProperties
import com.sorrowblue.cmpdestinations.DestinationDialogStyle

object DestinationStyleAdaptive : DestinationDialogStyle {
    override val dialogProperties = DialogProperties(
        usePlatformDefaultWidth = false,
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    )
}
