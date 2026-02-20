package com.sorrowblue.comicviewer.data.reader.zip.startup

import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import dev.zacsweers.metro.Inject
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.logcat
import net.sf.sevenzipjbinding.SevenZip

@Inject
internal class SevenZipInitializer : Initializer<Unit> {
    override fun create() {
        SevenZip.initSevenZipFromPlatformJAR()
        logcat(LogPriority.INFO) { "Initialized SevenZip. ${SevenZip.getSevenZipJBindingVersion()}." }
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> =
        listOf(LogcatInitializer::class)
}
