package com.sorrowblue.comicviewer.data.reader.zip.startup

import android.content.Context
import androidx.startup.Initializer
import com.sorrowblue.comicviewer.framework.common.LogcatInitializer
import logcat.LogPriority
import logcat.logcat
import net.sf.sevenzipjbinding.SevenZip

internal class SevenZipInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        SevenZip.initSevenZipFromPlatformJAR()
        logcat(
            LogPriority.INFO,
        ) { "Initialized SevenZip. ${SevenZip.getSevenZipJBindingVersion()}." }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> =
        listOf(LogcatInitializer::class.java)
}
