package com.sorrowblue.comicviewer.data.coil.startup

import com.sorrowblue.comicviewer.data.coil.BaseCoilInitializer
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import kotlin.reflect.KClass
import org.koin.core.annotation.Factory

@Factory
internal class CoilInitializer : BaseCoilInitializer(), Initializer<Unit> {
    override fun create() {
        initialize()
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> {
        return listOf(LogcatInitializer::class)
    }
}
