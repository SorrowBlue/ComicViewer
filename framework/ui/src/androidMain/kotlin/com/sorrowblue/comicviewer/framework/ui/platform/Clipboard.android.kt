/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.platform

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun String.createClipEntry(): ClipEntry = ClipEntry(ClipData.newPlainText(this, this))
