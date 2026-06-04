package com.sorrowblue.comicviewer.data.reader.zip.startup

import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.InitializerScope
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import dev.zacsweers.metro.ContributesIntoSet
import kotlin.reflect.KClass
import logcat.LogPriority
import logcat.logcat

@ContributesIntoSet(InitializerScope::class)
internal class KioArchInitializer : Initializer<Unit> {
    override fun create() {
        logcat(LogPriority.INFO) {
            ""
        }
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> =
        listOf(LogcatInitializer::class)
}
