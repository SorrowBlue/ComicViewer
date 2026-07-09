/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.info.license

interface LicenseeHelper {
    suspend fun loadLibraries(): ByteArray
}
