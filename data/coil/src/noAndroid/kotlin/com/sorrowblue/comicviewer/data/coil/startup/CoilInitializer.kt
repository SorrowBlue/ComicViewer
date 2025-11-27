package com.sorrowblue.comicviewer.data.coil.startup

import com.sorrowblue.comicviewer.data.coil.BaseCoilInitializer
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import kotlin.reflect.KClass

internal class CoilInitializer(private val context: PlatformContext) :
    BaseCoilInitializer(),
    Initializer<Unit> {
    override fun create() {
        initialize(context)
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> =
        listOf(LogcatInitializer::class)
}
