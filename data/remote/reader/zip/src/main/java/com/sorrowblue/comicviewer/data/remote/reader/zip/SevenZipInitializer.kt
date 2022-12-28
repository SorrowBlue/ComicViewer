package com.sorrowblue.comicviewer.data.remote.reader.zip

import android.content.Context
import androidx.startup.Initializer
import net.sf.sevenzipjbinding.SevenZip

internal class SevenZipInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        SevenZip.initSevenZipFromPlatformJAR()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }

}
