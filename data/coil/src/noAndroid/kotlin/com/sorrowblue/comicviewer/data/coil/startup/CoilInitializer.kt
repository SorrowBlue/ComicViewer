package com.sorrowblue.comicviewer.data.coil.startup

import coil3.PlatformContext
import com.sorrowblue.comicviewer.data.coil.BaseCoilInitializer
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import kotlin.reflect.KClass
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single

@Factory
internal class CoilInitializer : BaseCoilInitializer(), Initializer<Unit> {
    override fun create() {
        initialize()
    }

    override fun dependencies(): List<KClass<out Initializer<*>>?> {
        return listOf(LogcatInitializer::class)
    }
}

@Single
fun providePlatformContext() = PlatformContext.INSTANCE
