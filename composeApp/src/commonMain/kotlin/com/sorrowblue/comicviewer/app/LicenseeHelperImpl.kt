package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import comicviewer.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

internal class LicenseeHelperImpl : LicenseeHelper {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun loadLibraries(): ByteArray = Res.readBytes("files/aboutlibraries.json")
}
