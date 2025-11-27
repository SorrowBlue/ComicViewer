package com.sorrowblue.comicviewer.app

import android.content.Context
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper

internal class LicenseeHelperImpl(private val context: Context) : LicenseeHelper {

    override suspend fun loadLibraries(): ByteArray =
        context.resources.openRawResource(R.raw.aboutlibraries).readBytes()
}
