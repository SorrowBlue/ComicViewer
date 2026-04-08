package com.sorrowblue.comicviewer.data.coil.di

import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(DataScope::class)
interface NoAndroidDataCoilProviders {

    @Provides
    fun provideCoilPlatformContext(): coil3.PlatformContext = coil3.PlatformContext.INSTANCE
}
