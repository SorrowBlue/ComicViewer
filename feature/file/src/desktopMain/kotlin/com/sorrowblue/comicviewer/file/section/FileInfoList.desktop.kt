package com.sorrowblue.comicviewer.file.section

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.createClipEntry(): ClipEntry = ClipEntry(StringSelection(this))
