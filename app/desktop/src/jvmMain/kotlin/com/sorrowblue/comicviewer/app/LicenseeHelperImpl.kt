package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal class LicenseeHelperImpl : LicenseeHelper {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun loadLibraries(): ByteArray = ByteArray(0)
//        Res.readBytes("files/aboutlibraries.json")
}
