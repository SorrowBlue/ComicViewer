package com.sorrowblue.comicviewer.data.coil.startup

import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

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
