package com.sorrowblue.comicviewer.data.coil.di

import com.sorrowblue.comicviewer.data.coil.startup.CoilInitializer
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(DataScope::class)
interface NoAndroidDataCoilProviders {
    @Provides
    @IntoSet
    private fun provideCoilInitializer(context: PlatformContext): Initializer<*> =
        CoilInitializer(context)

    @Provides
    fun provideCoilPlatformContext(): coil3.PlatformContext = coil3.PlatformContext.INSTANCE
}
