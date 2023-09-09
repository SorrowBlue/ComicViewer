package com.sorrowblue.comicviewer.library

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Download
import androidx.compose.material.icons.twotone.History
import androidx.compose.ui.graphics.vector.ImageVector

enum class LocalFeature(val label: String, val icon: ImageVector) : Library {
    HISTORY("履歴", Icons.TwoTone.History),
    DOWNLOADED("ダウンロード", Icons.TwoTone.Download);
}
