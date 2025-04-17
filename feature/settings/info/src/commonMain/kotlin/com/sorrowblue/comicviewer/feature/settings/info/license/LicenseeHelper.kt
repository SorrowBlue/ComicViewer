package com.sorrowblue.comicviewer.feature.settings.info.license

interface LicenseeHelper {

    suspend fun loadAboutlibraries(): ByteArray
}
