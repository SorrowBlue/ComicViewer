/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import comicviewer.app.jvmapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal class LicenseeHelperImpl : LicenseeHelper {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun loadLibraries(): ByteArray = Res.readBytes("files/aboutlibraries.json")
}
