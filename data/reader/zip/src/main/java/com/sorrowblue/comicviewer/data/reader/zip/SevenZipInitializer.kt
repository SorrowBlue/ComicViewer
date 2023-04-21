package com.sorrowblue.comicviewer.data.reader.zip

import android.content.Context
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.framework.LogcatInitializer
import logcat.LogPriority
import logcat.logcat
import net.sf.sevenzipjbinding.SevenZip

internal class SevenZipInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        SevenZip.initSevenZipFromPlatformJAR()
        logcat(LogPriority.INFO) { "Initialized SevenZip. Version is ${SevenZip.getSevenZipJBindingVersion()}." }
    }

    override fun dependencies() = listOf(LogcatInitializer::class.java)
}
