package com.sorrowblue.comicviewer.file.section

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun String.createClipEntry(): ClipEntry = ClipEntry.withPlainText(this)
