package com.sorrowblue.comicviewer.file.section

import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

actual fun String.createClipEntry(): ClipEntry {
    return ClipEntry(StringSelection(this))
}
