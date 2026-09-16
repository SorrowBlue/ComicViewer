/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.ui.platform

import androidx.compose.ui.platform.ClipEntry

expect fun String.createClipEntry(): ClipEntry
