package com.sorrowblue.comicviewer.app

import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import comicviewer.composeapp.generated.resources.Res
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import org.jetbrains.compose.resources.ExperimentalResourceApi

@ContributesBinding(AppScope::class)
@Inject
internal class LicenseeHelperImpl : LicenseeHelper {
    @OptIn(ExperimentalResourceApi::class)
    override suspend fun loadLibraries(): ByteArray = Res.readBytes("files/aboutlibraries.json")
}
