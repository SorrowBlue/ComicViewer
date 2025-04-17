package com.sorrowblue.comicviewer.file.section

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun String.createClipEntry(): ClipEntry {
    return ClipEntry(ClipData.newPlainText(this, this))
}
