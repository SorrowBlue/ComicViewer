/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.info.license

import comicviewer.feature.settings.info.generated.resources.Res
import dev.zacsweers.metro.Inject

@Inject
internal class LicenseeHelper {
    suspend fun loadLibraries(): ByteArray = Res.readBytes("files/aboutlibraries.json")
}
