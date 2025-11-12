package com.sorrowblue.comicviewer.data.coil.startup

import com.sorrowblue.comicviewer.data.coil.BaseCoilInitializer
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
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

@ContributesTo(DataScope::class)
interface CoilProviders {
    @Provides
    @IntoSet
    private fun provideCoilInitializer(context: PlatformContext): Initializer<*> = CoilInitializer(
        context,
    )

    @Provides
    fun providePlatformContext(): coil3.PlatformContext = coil3.PlatformContext.INSTANCE
}
